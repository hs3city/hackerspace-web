package models.daos

import javax.inject.Inject

import models.Event
import models.db.{DBEvent, DBEventParticipant}
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future


/**
	* Created by lukmy on 10.02.2017.
	*/
class EventDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, val userDAO: UserDAO) extends DAOSlick {

	import driver.api._

	def getUserAttendance(userID: String, eventID: String): Future[Option[DBEventParticipant]] = {
		val query = slickEventParticipants.filter(_.eventID === eventID).filter(_.userID === userID)
		db.run(query.result.headOption)
	}

	def save(dbEventAttendance: DBEventParticipant) = {
		val query = slickEventParticipants.insertOrUpdate(dbEventAttendance)
		db.run(query).map(_ => dbEventAttendance)
	}

	def getById(eventId: String): Future[Option[DBEvent]] = {
		val query = slickEvents.filter(_.id === eventId)
		db.run(query.result.headOption)
	}

	def save(event: DBEvent): Future[DBEvent] = {
		val query = slickEvents.insertOrUpdate(event)
		db.run(query).map(_ => event)
	}

	def all(): Future[Seq[DBEvent]] = {
		eventsWithAttendance()
		db.run(slickEvents.result)
	}

	def eventsWithAttendance(): Future[Seq[Event]] = {
		val query = slickEvents joinLeft slickEventParticipants on (_.id === _.eventID) joinLeft slickUsers on (_._2.map(_.userID) === _.id)

		db.run(query.result).map { seq =>
			seq.map {
				case ((e1, e2), e3) => (e1, e2, e3)
			}.groupBy(_._1).mapValues { collection =>
				collection.map {
					case (_, participant, user) => (participant, user)
				} collect {
					case (Some(a), Some(b)) => a -> b
				}
			}
		}.map { seq =>
			seq.map {
				case (event, data) => Event(event, data)
			}.toSeq
		}
	}
}
