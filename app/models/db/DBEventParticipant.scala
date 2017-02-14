package models.db

import models.EventAttendanceStatus.EventAttendanceStatus

case class DBEventParticipant(id: Option[Long],
                              eventID: Long,
                              userID: String,
                              status: EventAttendanceStatus
                             )