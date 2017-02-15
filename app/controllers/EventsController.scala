package controllers


import java.util.UUID

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Silhouette
import forms.EventForm
import models.db.DBEvent
import models.services.EventService
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.Controller
import utils.DefaultEnv

import scala.concurrent.Future


/**
  * Created by lukmy on 10.02.2017.
  */
class EventsController @Inject()(val messagesApi: MessagesApi, silhouette: Silhouette[DefaultEnv], eventService: EventService) extends Controller with I18nSupport {

  def events = silhouette.SecuredAction.async { implicit request =>
    eventService.all().flatMap {
      events => Future.successful(Ok(views.html.event.events(request.identity, events)))
    }
  }

  def newEvent = silhouette.SecuredAction.async { implicit request =>
    Future.successful(Ok(views.html.event.newEvent(request.identity, EventForm.form)))
  }

  def save = silhouette.SecuredAction.async { implicit request =>
    EventForm.form.bindFromRequest.fold(
      form => Future.successful(BadRequest(views.html.event.newEvent(request.identity, form))),
      data => {
        val event = DBEvent(UUID.randomUUID().toString, data.title, request.identity.userID.toString, data.description, data.startTime, data.endTime)
        eventService.save(event).flatMap { event => Future.successful(Ok(views.html.event.events(request.identity, Seq())))
        }
      }
    )
  }

}
