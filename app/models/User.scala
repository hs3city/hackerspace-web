package models

import java.util.UUID

import com.mohiva.play.silhouette.api.{Identity, LoginInfo}
import models.db.DBUser

import scala.language.implicitConversions

/**
 * The user object.
 *
 * @param userID The unique ID of the user.
 * @param loginInfo The linked login info.
 * @param firstName Maybe the first name of the authenticated user.
 * @param lastName Maybe the last name of the authenticated user.
 * @param fullName Maybe the full name of the authenticated user.
 * @param email Maybe the email of the authenticated provider.
 * @param avatarURL Maybe the avatar URL of the authenticated provider.
 */
case class User(
  userID: UUID,
  loginInfo: LoginInfo,
  firstName: Option[String],
  lastName: Option[String],
  fullName: Option[String],
  email: Option[String],
  avatarURL: Option[String]) extends Identity


object User{
	implicit def toDbUser(user: User): DBUser = {
		DBUser(user.userID.toString, user.firstName, user.lastName, user.fullName, user.email, user.avatarURL)
	}
}

