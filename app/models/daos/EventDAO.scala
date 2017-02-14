package models.daos

import javax.inject.Inject

import models.db.{DBEvent, DBEventParticipant}
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future

/**
  * Created by lukmy on 10.02.2017.
  */
class EventDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, val userDAO: UserDAO) extends DAOSlick {

  import driver.api._

  def save(event: DBEvent) : Future[DBEvent] = {
    val eventReturningId = slickEvents returning slickEvents.map(_.id) into ((_, newId) => event.copy(id = newId))
    val query = eventReturningId.insertOrUpdate(event)
    db.run(query).map(_ => event)
  }

  def all(): Future[Seq[DBEvent]] = {
    db.run(slickEvents.result)
  }

  def saveAttendance(eventAttendance: DBEventParticipant) : Future[DBEventParticipant] = {
    val eventsParticipantsReturningId = slickEventParticipants returning slickEventParticipants.map(_.id) into ((_, newId) => eventAttendance.copy(id = Some(newId)))
    val query = eventsParticipantsReturningId.insertOrUpdate(eventAttendance)
    db.run(query).map(_ => eventAttendance)
  }
}
