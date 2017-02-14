package models.services

import com.google.inject.Inject
import models.daos.EventDAO
import models.db.DBEvent

import scala.concurrent.Future

/**
  * Created by lukmy on 10.02.2017.
  */
class EventService @Inject()(eventDAO: EventDAO) {


  def save(event: DBEvent): Future[DBEvent] = eventDAO.save(event)

  def all() : Future[Seq[DBEvent]] = eventDAO.all()
}
