package models.db

import org.joda.time.DateTime

case class DBEvent(
                    id: String,
                    title: String,
                    host: String,
                    description: String,
                    startTime: DateTime,
                    endTime: DateTime
                  )
