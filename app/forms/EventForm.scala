package forms

import play.api.data.Form
import play.api.data.Forms._
import org.joda.time.DateTime

object EventForm {

  val form = Form(
    mapping(
      "title" -> nonEmptyText,
      "description" -> nonEmptyText,
      "startTime" -> jodaDate,
      "endTime" -> jodaDate
    )(Data.apply)(Data.unapply)
  )

  case class Data(
                   title: String,
                   description: String,
                   startTime: DateTime,
                   endTime: DateTime)

}
