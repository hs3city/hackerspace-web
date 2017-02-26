package controllers


import java.util.UUID

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Silhouette
import forms.{EventAttendanceForm, EventForm}
import models.db.{DBEvent, DBEventParticipant}
import models.services.EventService
import models.{Event, EventAttendanceStatus}
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

	def event(eventId: String) = silhouette.SecuredAction.async { implicit request =>
		eventService.getEvent(eventId).map {
			event => Ok(views.html.event.event(request.identity, event, EventAttendanceForm.form))
		}
	}

	def newEvent = silhouette.SecuredAction.async { implicit request =>
		Future.successful(Ok(views.html.event.newEvent(request.identity, EventForm.form)))
	}

	def updateAttendance(eventId: String) = silhouette.SecuredAction.async { implicit request =>

		def saveAttendance(event: Event, data: EventAttendanceForm.Data) = {
			val dbEventAttendance = DBEventParticipant(event.dBEvent.id, request.identity.userID.toString, EventAttendanceStatus(data.attendance))
				eventService.save(dbEventAttendance).flatMap { dBEventParticipant => {
					eventService.getEvent(dBEventParticipant.eventID).map {
						updatedEvent => Ok(views.html.event.event(request.identity, updatedEvent, EventAttendanceForm.form))
					}
				}
			}
		}


		eventService.getEvent(eventId).flatMap {
			event =>
				EventAttendanceForm.form.bindFromRequest().fold(
					formWithErrors => Future.successful(BadRequest(views.html.event.event(request.identity, event, formWithErrors))),
					data => saveAttendance(event, data)
				)
		}
	}

	def save = silhouette.SecuredAction.async { implicit request =>
		EventForm.form.bindFromRequest.fold(
			formWithErrors => Future.successful(BadRequest(views.html.event.newEvent(request.identity, formWithErrors))),
			data => {
				val event = DBEvent(UUID.randomUUID().toString, data.title, request.identity.userID.toString, data.description, data.startTime, data.endTime)
				for {
					event <- eventService.save(event)
					_ <- eventService.save(DBEventParticipant(event.id, event.host, EventAttendanceStatus.Confirmed))
				} yield Redirect(routes.EventsController.events())
			}
		)
	}

	def getEventAttendance(eventParticipantOption: Option[DBEventParticipant]): Int = {
		eventParticipantOption.map(eventParticipant => eventParticipant.status.id).getOrElse(EventAttendanceStatus.NotAttending.id)
	}
}
