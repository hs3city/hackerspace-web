package controllers


import java.util.UUID

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Silhouette
import forms.{EventAttendanceForm, EventForm}
import models.EventAttendanceStatus
import models.db.{DBEvent, DBEventParticipant}
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

	def event(eventId: String) = silhouette.SecuredAction.async { implicit request =>
		(for {
			event <- eventService.getById(eventId)
			userEventAttendance <- eventService.getUserAtttendance(request.identity.userID.toString, event.get.id)
		} yield (event, userEventAttendance)).map {
			case (event, userEventAttendance) =>
				Ok(views.html.event.event(request.identity, event.get, getEventAttendance(userEventAttendance), EventAttendanceForm.form))
		}
	}

	def newEvent = silhouette.SecuredAction.async { implicit request =>
		Future.successful(Ok(views.html.event.newEvent(request.identity, EventForm.form)))
	}

	def updateAttendance(eventId: String) = silhouette.SecuredAction.async { implicit request =>

		def saveAttendance(event: DBEvent, data: EventAttendanceForm.Data) = {
			val dbEventAttendance = DBEventParticipant(event.id, request.identity.userID.toString, data.attendance)
			eventService.save(dbEventAttendance).map { dBEventParticipant => Ok(views.html.event.event(request.identity, event, getEventAttendance(Some(dBEventParticipant)), EventAttendanceForm.form))
			}
		}

		eventService.getById(eventId).flatMap {
			event =>
				EventAttendanceForm.form.bindFromRequest().fold(
					formWithErrors => Future.successful(BadRequest(views.html.event.event(request.identity, event.get, getEventAttendance(None), formWithErrors))),
					data => saveAttendance(event.get, data)
				)
		}
	}

	def save = silhouette.SecuredAction.async { implicit request =>
		EventForm.form.bindFromRequest.fold(
			formWithErrors => Future.successful(BadRequest(views.html.event.newEvent(request.identity, formWithErrors))),
			data => {
				val event = DBEvent(UUID.randomUUID().toString, data.title, request.identity.userID.toString, data.description, data.startTime, data.endTime)
				eventService.save(event).flatMap { _ => Future.successful(Redirect(routes.EventsController.events()))
				}
			}
		)
	}

	def getEventAttendance(eventParticipantOption: Option[DBEventParticipant]): Int = {
		eventParticipantOption.map(eventParticipant => eventParticipant.status).getOrElse(EventAttendanceStatus.NotAttending.id)
	}


}
