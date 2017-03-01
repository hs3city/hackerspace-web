package models

import models.db.{DBEventComment, DBUser}


case class EventComment(user: DBUser,
												dBEventComment: DBEventComment)