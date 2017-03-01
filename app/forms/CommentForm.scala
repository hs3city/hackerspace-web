package forms

import play.api.data.Form
import play.api.data.Forms._

object CommentForm {

  val form = Form(
    mapping(
      "comment" -> nonEmptyText
    )(Data.apply)(Data.unapply)
  )

  case class Data(comment: String)
}
