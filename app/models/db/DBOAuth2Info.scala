package models.db

case class DBOAuth2Info(
                         id: Option[Long],
                         accessToken: String,
                         tokenType: Option[String],
                         expiresIn: Option[Int],
                         refreshToken: Option[String],
                         loginInfoId: Long
                       )