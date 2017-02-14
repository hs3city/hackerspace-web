package models.db

case class DBUser(
                   userID: String,
                   firstName: Option[String],
                   lastName: Option[String],
                   fullName: Option[String],
                   email: Option[String],
                   avatarURL: Option[String]
               )