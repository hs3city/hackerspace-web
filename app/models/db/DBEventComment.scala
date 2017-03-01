package models.db

import org.joda.time.DateTime

case class DBEventComment(id: Option[Long], eventId: String, userId: String, comment: String, datetime: DateTime)
