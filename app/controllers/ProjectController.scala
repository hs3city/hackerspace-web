package controllers

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import models.User
import play.api.i18n.MessagesApi
import play.api.mvc.Action

import scala.concurrent.Future

class ProjectController @Inject()(
	                              val messagesApi: MessagesApi,
	                              val env: Environment[User, CookieAuthenticator])
	extends Silhouette[User, CookieAuthenticator] {

	def projects = SecuredAction.async { implicit request =>
		Future.successful(Ok(views.html.project.projects(request.identity)))
	}

}
