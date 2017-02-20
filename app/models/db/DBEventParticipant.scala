package models.db

case class DBEventParticipant(eventID: String,
                              userID: String,
                              status: Int)