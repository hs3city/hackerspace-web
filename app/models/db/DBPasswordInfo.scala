package models.db

case class DBPasswordInfo(
                           hasher: String,
                           password: String,
                           salt: Option[String],
                           loginInfoId: Long
                         )
