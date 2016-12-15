package controllers

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import models.User
import models.services.UserService
import play.api.i18n.MessagesApi

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class UserController @Inject()(
	                              val messagesApi: MessagesApi,
	                              val userService: UserService,
	                              val env: Environment[User, CookieAuthenticator])
	extends Silhouette[User, CookieAuthenticator] {

	def users = SecuredAction.async { implicit request =>
		userService.members().flatMap { users => {
			Future.successful(Ok(views.html.user.users(request.identity, users)))
			}
		}
	}
}
