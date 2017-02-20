package models.services

import com.google.inject.Inject
import models.Event
import models.daos.EventDAO
import models.db.{DBEvent, DBEventParticipant}

import scala.concurrent.Future

/**
  * Created by lukmy on 10.02.2017.
  */
class EventService @Inject()(eventDAO: EventDAO) {

  def getUserAtttendance(userID: String, eventID: String) = eventDAO.getUserAttendance(userID, eventID)

  def getById(eventId: String): Future[Option[DBEvent]] = eventDAO.getById(eventId)

  def save(event: DBEvent): Future[DBEvent] = eventDAO.save(event)

  def save(dbEventAttendance: DBEventParticipant) = eventDAO.save(dbEventAttendance)

  def all() : Future[Seq[Event]] = eventDAO.eventsWithAttendance()
}
