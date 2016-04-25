package models

import org.bson.types.ObjectId

/**
  * Created by erh on 20.04.16.
  */
case class Connection(
                       _id: String,
                       name: String,
                       dbType: String,
                       dbName: String
                     )

object Connection {

  import play.api.libs.json._

  implicit val connectionFormat = Json.format[Connection]
}
