package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.providers._
import models.User
import models.services.UserService
import play.api.i18n.{Messages, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.{Action, AnyContent, Request, Result}

import scala.concurrent.Future

/**
  * The social auth controller.
  *
  * @param messagesApi            The Play messages API.
  * @param env                    The Silhouette environment.
  * @param userService            The user service implementation.
  * @param authInfoRepository     The auth info service implementation.
  * @param socialProviderRegistry The social provider registry.
  */
class SocialAuthController @Inject()(
                                      val messagesApi: MessagesApi,
                                      val env: Environment[User, CookieAuthenticator],
                                      userService: UserService,
                                      authInfoRepository: AuthInfoRepository,
                                      socialProviderRegistry: SocialProviderRegistry)
  extends Silhouette[User, CookieAuthenticator] with Logger {


  /**
    * Authenticates a user against a social provider.
    *
    * @param provider The ID of the provider to authenticate against.
    * @return The result to display.
    */
  def authenticate(provider: String) = Action.async { implicit request =>
    (socialProviderRegistry.get[SocialProvider](provider) match {
      case Some(p: SocialProvider with CommonSocialProfileBuilder) =>
        p.authenticate().flatMap {
          case Left(result) => Future.successful(result)
          case Right(authInfo) => checkIfUserRegistered(p, authInfo)
        }
      case _ => Future.failed(new ProviderException(s"Cannot authenticate with unexpected social provider $provider"))
    }).recover {
      case e: ProviderException =>
        logger.error("Unexpected provider error", e)
        Redirect(routes.ApplicationController.signIn()).flashing("error" -> Messages("could.not.authenticate"))
    }
  }

  def checkIfUserRegistered(p: SocialProvider with CommonSocialProfileBuilder, authInfo: AuthInfo)(implicit request: Request[AnyContent]): Future[Result] = {
    (for {
      profile <- p.retrieveProfile(authInfo.asInstanceOf[p.A])
      userOption <- userService.findByEmail(profile.email.get)
    } yield {
        userOption match{
          case None => completeAuthProcess(p, profile, authInfo)
          case Some(user) => {
            if(user.loginInfo == profile.loginInfo){
              completeAuthProcess(p, profile, authInfo)
            }else{
              Future.successful(Redirect(routes.ApplicationController.signIn()).flashing("error" -> Messages("user.already.registered")))
            }
          }
        }
    }).flatMap(identity)
  }

  def completeAuthProcess(p: SocialProvider with CommonSocialProfileBuilder, profile: Any, authInfo: AuthInfo)(implicit request: Request[AnyContent]): Future[Result] = {
    val profileCasted: p.Profile = profile.asInstanceOf[p.Profile]
    for {
      user <- userService.save(profileCasted)
      authInfo <- authInfoRepository.save(profileCasted.loginInfo, authInfo)
      authenticator <- env.authenticatorService.create(profileCasted.loginInfo)
      value <- env.authenticatorService.init(authenticator)
      result <- env.authenticatorService.embed(value, Redirect(routes.ApplicationController.index()))
    } yield {
      env.eventBus.publish(LoginEvent(user, request, request2Messages))
      result
    }
  }

}
