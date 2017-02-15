package controllers

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Silhouette
import models.services.UserService
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Controller
import utils.DefaultEnv

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UserController @Inject()(
	                              val messagesApi: MessagesApi,
	                              userService: UserService,
																silhouette: Silhouette[DefaultEnv])
	extends Controller with I18nSupport  {

	def users = silhouette.SecuredAction.async { implicit request =>
		userService.members().flatMap { users => {
			Future.successful(Ok(views.html.user.users(request.identity, users)))
			}
		}
	}
}
