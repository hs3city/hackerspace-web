package models

import org.joda.time.DateTime

/**
  * Created by lukmy on 10.02.2017.
  */
case class Event(
                  eventID: Int,
                  title: String,
                  host: User,
                  description: String,
                  startTime: DateTime,
                  endTime: DateTime,
                  participants: Seq[User]
                )
