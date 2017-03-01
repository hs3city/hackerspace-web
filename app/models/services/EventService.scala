package models.services

import com.google.inject.Inject
import models.Event
import models.daos.EventDAO
import models.db.{DBEvent, DBEventComment, DBEventParticipant}

import scala.concurrent.Future

/**
  * Created by lukmy on 10.02.2017.
  */
class EventService @Inject()(eventDAO: EventDAO) {

	def getEvent(eventId: String) = eventDAO.getEvent(eventId)

	def getEventComments(id: String) = eventDAO.getEventComments(id)

  def getUserAttendance(userID: String, eventID: String) = eventDAO.getUserAttendance(userID, eventID)

  def getById(eventId: String): Future[Option[DBEvent]] = eventDAO.getById(eventId)

  def save(event: DBEvent): Future[DBEvent] = eventDAO.save(event)

  def save(dbEventAttendance: DBEventParticipant) = eventDAO.save(dbEventAttendance)

	def save(dbComment: DBEventComment) = eventDAO.save(dbComment)

  def all() : Future[Seq[Event]] = eventDAO.eventsWithAttendance()
}
