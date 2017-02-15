package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.{LogoutEvent, Silhouette}
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry
import forms._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Controller
import utils.DefaultEnv

import scala.concurrent.ExecutionContext.Implicits.global


import scala.concurrent.Future

class ApplicationController @Inject()(
	                                     val messagesApi: MessagesApi,
																			 val silhouette: Silhouette[DefaultEnv],
	                                     socialProviderRegistry: SocialProviderRegistry)
	extends Controller with I18nSupport {

	/**
		* Handles the index action.
		*
		* @return The result to display.
		*/
	def index = silhouette.SecuredAction.async { implicit request =>
		Future.successful(Ok(views.html.home(request.identity)))
	}

	def dashboard() = silhouette.SecuredAction.async { implicit request =>
		Future.successful(Ok(views.html.dashboard(request.identity)))
	}

	/**
		* Handles the Sign In action.
		*
		* @return The result to display.
		*/
	def signIn = silhouette.UserAwareAction.async { implicit request =>
		request.identity match {
			case Some(user) => Future.successful(Redirect(routes.ApplicationController.index()))
			case None => Future.successful(Ok(views.html.signIn(SignInForm.form, socialProviderRegistry)))
		}
	}

	/**
		* Handles the Sign Up action.
		*
		* @return The result to display.
		*/
	def signUp = silhouette.UserAwareAction.async { implicit request =>
		request.identity match {
			case Some(user) => Future.successful(Redirect(routes.ApplicationController.index()))
			case None => Future.successful(Ok(views.html.signUp(SignUpForm.form)))
		}
	}

	/**
		* Handles the Sign Out action.
		*
		* @return The result to display.
		*/
	def signOut = silhouette.SecuredAction.async { implicit request =>
		val result = Redirect(routes.ApplicationController.index())
		silhouette.env.eventBus.publish(LogoutEvent(request.identity, request))
		silhouette.env.authenticatorService.discard(request.authenticator, result)
	}
}
