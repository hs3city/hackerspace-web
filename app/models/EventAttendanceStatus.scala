package models

/**
  * Created by lukmy on 10.02.2017.
  */
object EventAttendanceStatus extends Enumeration{
  type EventAttendanceStatus = Value
  val Confirmed = Value("confirmed")
  val Tentative = Value("tentative")
  val NotAttending = Value("notAttending")
}
