package utils

import com.mohiva.play.silhouette.api.actions.UnsecuredErrorHandler
import play.api.mvc.RequestHeader
import play.api.mvc.Results._

import scala.concurrent.Future

/**
  * Created by lukmy on 16.02.2017.
  */
class CustomUnsecuredErrorHandler extends UnsecuredErrorHandler{

  override def onNotAuthorized(implicit request: RequestHeader) = {
    Future.successful(Redirect(controllers.routes.ApplicationController.index()))
  }

}
