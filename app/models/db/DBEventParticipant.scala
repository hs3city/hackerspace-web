package models.db

import models.EventAttendanceStatus.EventAttendanceStatus

case class DBEventParticipant(eventID: String,
                              userID: String,
                              status: EventAttendanceStatus)