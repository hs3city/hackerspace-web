package models

/**
  * Created by lukmy on 10.02.2017.
  */
object EventAttendanceStatus extends Enumeration{
  type EventAttendanceStatus = Value
  val Confirmed = Value("Confirmed")
  val Tentative = Value("Tentative")
  val NotAttending = Value("Not attending")
}
