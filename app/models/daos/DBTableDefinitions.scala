package models.daos

import com.github.tototoshi.slick.MySQLJodaSupport._
import com.mohiva.play.silhouette.api.LoginInfo
import models.EventAttendanceStatus
import models.EventAttendanceStatus.EventAttendanceStatus
import models.db._
import org.joda.time.DateTime
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape.proveShapeOf

trait DBTableDefinitions {

	protected val driver: JdbcProfile

	import driver.api._

	implicit lazy val eventAttendanceStatusMapping = MappedColumnType.base[EventAttendanceStatus, String](
		e => e.toString,
		s => EventAttendanceStatus.withName(s)
	)

	class Users(tag: Tag) extends Table[DBUser](tag, "user") {
		def id = column[String]("userID", O.PrimaryKey)

		def firstName = column[Option[String]]("firstName")

		def lastName = column[Option[String]]("lastName")

		def fullName = column[Option[String]]("fullName")

		def email = column[Option[String]]("email")

		def avatarURL = column[Option[String]]("avatarURL")

		def * = (id, firstName, lastName, fullName, email, avatarURL) <> (DBUser.tupled, DBUser.unapply)
	}

	class LoginInfos(tag: Tag) extends Table[DBLoginInfo](tag, "logininfo") {
		def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

		def providerID = column[String]("providerID")

		def providerKey = column[String]("providerKey")

		def * = (id.?, providerID, providerKey) <> (DBLoginInfo.tupled, DBLoginInfo.unapply)
	}


	class UserLoginInfos(tag: Tag) extends Table[DBUserLoginInfo](tag, "userlogininfo") {
		def userID = column[String]("userID")

		def loginInfoId = column[Long]("loginInfoId")

		def * = (userID, loginInfoId) <> (DBUserLoginInfo.tupled, DBUserLoginInfo.unapply)
	}


	class PasswordInfos(tag: Tag) extends Table[DBPasswordInfo](tag, "passwordinfo") {
		def hasher = column[String]("hasher")

		def password = column[String]("password")

		def salt = column[Option[String]]("salt")

		def loginInfoId = column[Long]("loginInfoId")

		def * = (hasher, password, salt, loginInfoId) <> (DBPasswordInfo.tupled, DBPasswordInfo.unapply)
	}



	class OAuth2Infos(tag: Tag) extends Table[DBOAuth2Info](tag, "oauth2info") {
		def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

		def accessToken = column[String]("accesstoken")

		def tokenType = column[Option[String]]("tokentype")

		def expiresIn = column[Option[Int]]("expiresin")

		def refreshToken = column[Option[String]]("refreshtoken")

		def loginInfoId = column[Long]("logininfoid")

		def * = (id.?, accessToken, tokenType, expiresIn, refreshToken, loginInfoId) <> (DBOAuth2Info.tupled, DBOAuth2Info.unapply)
	}

	class Events(tag: Tag) extends Table[DBEvent](tag, "event") {

		def id = column[String]("id", O.PrimaryKey)

		def title = column[String]("title")

		def host = column[String]("host")

		def description = column[String]("description")

		def startTime = column[DateTime]("startTime")

		def endTime = column[Option[DateTime]]("endTime")

		def * = (id, title, host, description, startTime, endTime) <> (DBEvent.tupled, DBEvent.unapply)
	}

	class EventParticipants(tag: Tag) extends Table[DBEventParticipant](tag, "event_participants") {

		def eventID = column[String]("eventID", O.PrimaryKey)

		def userID = column[String]("userID", O.PrimaryKey)

		def status = column[Int]("status")

		def * = (eventID, userID, status) <> (DBEventParticipant.tupled, DBEventParticipant.unapply)
	}

	// table query definitions
	val slickUsers = TableQuery[Users]
	val slickLoginInfos = TableQuery[LoginInfos]
	val slickUserLoginInfos = TableQuery[UserLoginInfos]
	val slickPasswordInfos = TableQuery[PasswordInfos]
	val slickOAuth2Infos = TableQuery[OAuth2Infos]
	val slickEvents = TableQuery[Events]
	val slickEventParticipants = TableQuery[EventParticipants]

	// queries used in multiple places
	def loginInfoQuery(loginInfo: LoginInfo) =
		slickLoginInfos.filter(dbLoginInfo => dbLoginInfo.providerID === loginInfo.providerID && dbLoginInfo.providerKey === loginInfo.providerKey)
}
