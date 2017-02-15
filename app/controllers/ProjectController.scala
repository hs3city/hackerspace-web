package controllers

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Silhouette
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Controller
import utils.DefaultEnv

import scala.concurrent.Future

class ProjectController @Inject()(
	                              val messagesApi: MessagesApi,
																silhouette: Silhouette[DefaultEnv])
	extends Controller with I18nSupport {

	def projects = silhouette.SecuredAction.async { implicit request =>
		Future.successful(Ok(views.html.project.projects(request.identity)))
	}

}
