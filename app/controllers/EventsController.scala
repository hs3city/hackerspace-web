package controllers


import java.util.UUID

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Silhouette
import forms.{CommentForm, EventAttendanceForm, EventForm}
import models.db.{DBEvent, DBEventComment, DBEventParticipant}
import models.services.EventService
import models.{Event, EventAttendanceStatus}
import org.joda.time.DateTime
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.Controller
import utils.DefaultEnv

import scala.concurrent.Future

class EventsController @Inject()(val messagesApi: MessagesApi, silhouette: Silhouette[DefaultEnv], eventService: EventService) extends Controller with I18nSupport {

	def events = silhouette.SecuredAction.async { implicit request =>
		eventService.all().flatMap {
			events => Future.successful(Ok(views.html.event.events(request.identity, events)))
		}
	}

	def saveComment(eventId: String) = silhouette.SecuredAction.async { implicit request =>
		CommentForm.form.bindFromRequest.fold(
			formWithErrors => {
				for{
					event <- eventService.getEvent(eventId)
					comments <- eventService.getEventComments(eventId)
				} yield BadRequest(views.html.event.event(request.identity, event, comments, EventAttendanceForm.form, formWithErrors))
			},
			data => {
				val dbComment = DBEventComment(None, eventId, request.identity.userID.toString, data.comment, DateTime.now())
				eventService.save(dbComment).map {
					_ => Redirect(routes.EventsController.event(eventId))
				}
			}
		)
	}

	def event(eventId: String) = silhouette.SecuredAction.async { implicit request =>
		for {
			event <- eventService.getEvent(eventId)
			event_comments <- eventService.getEventComments(event.dBEvent.id)
		} yield Ok(views.html.event.event(request.identity, event, event_comments, EventAttendanceForm.form, CommentForm.form))
	}

	def newEvent = silhouette.SecuredAction.async { implicit request =>
		Future.successful(Ok(views.html.event.newEvent(request.identity, EventForm.form)))
	}

	def updateAttendance(eventId: String) = silhouette.SecuredAction.async { implicit request =>

		def saveAttendance(event: Event, data: EventAttendanceForm.Data) = {
			val dbEventAttendance = DBEventParticipant(event.dBEvent.id, request.identity.userID.toString, EventAttendanceStatus(data.attendance))
			for {
				dBEventParticipant <- eventService.save(dbEventAttendance)
				updatedEvent <- eventService.getEvent(dBEventParticipant.eventID)
				comments <- eventService.getEventComments(event.dBEvent.id)
			} yield Ok(views.html.event.event(request.identity, updatedEvent, comments, EventAttendanceForm.form, CommentForm.form))
		}

		(for{
			event <- eventService.getEvent(eventId)
			comments <- eventService.getEventComments(event.dBEvent.id)
		} yield EventAttendanceForm.form.bindFromRequest().fold(
			formWithErrors => Future.successful(BadRequest(views.html.event.event(request.identity, event, comments, formWithErrors, CommentForm.form))),
			data => saveAttendance(event, data)
		)).flatMap(identity)
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
