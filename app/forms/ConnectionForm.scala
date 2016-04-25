package forms

import play.api.data._
import play.api.data.Forms._

object ConnectionForm {

  case class ConnectionData(id: Option[String], name: String, dbType: String, dbName: String)

  val connectionForm = Form(
    mapping(
      "_id" -> optional(text),
      "name" -> text,
      "dbType" -> text,
      "dbName" -> text
    )(ConnectionData.apply)(ConnectionData.unapply)
  )
}
