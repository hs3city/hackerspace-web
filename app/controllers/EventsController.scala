package controllers


import java.util.UUID

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import forms.EventForm
import models.User
import models.db.DBEvent
import models.services.EventService
import play.api.i18n.MessagesApi

import scala.concurrent.Future

import play.api.libs.concurrent.Execution.Implicits._


/**
  * Created by lukmy on 10.02.2017.
  */
class EventsController @Inject()(val messagesApi: MessagesApi, val env: Environment[User, CookieAuthenticator], val eventService: EventService) extends Silhouette[User, CookieAuthenticator] {

  def events = SecuredAction.async { implicit request =>
    eventService.all().flatMap {
      events => Future.successful(Ok(views.html.event.events(request.identity, events)))
    }
  }

  def newEvent = SecuredAction.async { implicit request =>
    Future.successful(Ok(views.html.event.newEvent(request.identity, EventForm.form)))
  }

  def save = SecuredAction.async { implicit request =>
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
