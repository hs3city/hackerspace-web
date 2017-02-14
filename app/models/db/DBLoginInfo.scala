package models.db

case class DBLoginInfo(
                        id: Option[Long],
                        providerID: String,
                        providerKey: String
                      )