package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.Authenticator.Implicits._
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.{Clock, Credentials}
import com.mohiva.play.silhouette.impl.exceptions.IdentityNotFoundException
import com.mohiva.play.silhouette.impl.providers._
import forms.SignInForm
import models.services.UserService
import net.ceedubs.ficus.Ficus._
import play.api.Configuration
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc._
import utils.DefaultEnv

import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

class CredentialsAuthController @Inject() (
	                                          val messagesApi: MessagesApi,
                                            silhouette: Silhouette[DefaultEnv],
                                            userService: UserService,
	                                          authInfoRepository: AuthInfoRepository,
	                                          credentialsProvider: CredentialsProvider,
	                                          socialProviderRegistry: SocialProviderRegistry,
	                                          configuration: Configuration,
	                                          clock: Clock)
  extends Controller with I18nSupport {

  /**
   * Authenticates a user against the credentials provider.
   *
   * @return The result to display.
   */
  def authenticate = silhouette.UnsecuredAction.async { implicit request =>
    SignInForm.form.bindFromRequest.fold(
      form => Future.successful(BadRequest(views.html.signIn(form, socialProviderRegistry))),
      data => validateDuplicateRegistration(data)
    )
  }

  def validateDuplicateRegistration(data: SignInForm.Data)(implicit request: Request[AnyContent]): Future[Result] = {
    val credentials = Credentials(data.email, data.password)
        userService.findByEmail(data.email).flatMap{
            case Some(user) =>
              if(user.loginInfo.providerID == "credentials"){
                continueOath(credentials, data)
              }else{
                Future.successful(Redirect(routes.ApplicationController.signIn()).flashing("error" -> Messages("user.exists")))
              }
            case None => continueOath(credentials, data)
        }
  }

  def continueOath(credentials: Credentials, data: SignInForm.Data)(implicit request: Request[AnyContent]) : Future[Result] = credentialsProvider.authenticate(credentials).flatMap { loginInfo =>
    val result = Redirect(routes.ApplicationController.index())
    userService.retrieve(loginInfo).flatMap {
      case Some(user) =>
        val c = configuration.underlying
        silhouette.env.authenticatorService.create(loginInfo).map {
          case authenticator if data.rememberMe =>
            authenticator.copy(
              expirationDateTime = clock.now + c.as[FiniteDuration]("silhouette.authenticator.rememberMe.authenticatorExpiry"),
              idleTimeout = c.getAs[FiniteDuration]("silhouette.authenticator.rememberMe.authenticatorIdleTimeout"),
              cookieMaxAge = c.getAs[FiniteDuration]("silhouette.authenticator.rememberMe.cookieMaxAge")
            )
          case authenticator => authenticator
        }.flatMap { authenticator =>
          silhouette.env.eventBus.publish(LoginEvent(user, request))
          silhouette.env.authenticatorService.init(authenticator).flatMap { v =>
            silhouette.env.authenticatorService.embed(v, result)
          }
        }
      case None => Future.failed(new IdentityNotFoundException("Couldn't find user"))
    }
  }.recover {
    case e: ProviderException =>
      Redirect(routes.ApplicationController.signIn()).flashing("error" -> Messages("invalid.credentials"))
  }

}
