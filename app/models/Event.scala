package models

import models.db.{DBEvent, DBEventParticipant, DBUser}

case class Event(dBEvent: DBEvent, participants: Seq[(DBEventParticipant, DBUser)])
