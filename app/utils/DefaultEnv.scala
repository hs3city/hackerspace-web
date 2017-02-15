package utils

import com.mohiva.play.silhouette.api.Env
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import models.User

/**
  * Created by lukmy on 15.02.2017.
  */
trait DefaultEnv extends Env {
  type I = User
  type A = CookieAuthenticator
}
