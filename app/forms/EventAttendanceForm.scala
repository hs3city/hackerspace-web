package forms

import play.api.data.Form
import play.api.data.Forms.{mapping, _}


object EventAttendanceForm {
	val form = Form(
		mapping(
			"attendance" -> number
		)(Data.apply)(Data.unapply)
	)

	case class Data(attendance: Int)
}
