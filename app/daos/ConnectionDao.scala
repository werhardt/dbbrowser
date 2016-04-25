package daos

import com.google.inject.{ImplementedBy, Inject, Singleton}
import models.Connection
import org.mongodb.scala._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.result._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import services.Mongo

import scala.concurrent.Future

/**
  * Created by erh.
  */
@ImplementedBy(classOf[MongoConnectionDao])
trait ConnectionDao {

  def find(connectionId: String): Future[Connection]

  def findAll: Future[Seq[Connection]]

  def update(connection: Connection): Future[UpdateResult]

  def save(connection: Connection): Future[Completed]

  def delete(sessionId: String): Future[DeleteResult]
}

@Singleton
class MongoConnectionDao @Inject()(mongo: Mongo) extends ConnectionDao {

  private val connections: MongoCollection[Document] = mongo.db.getCollection("connection")

  override def find(connectionId: String): Future[Connection] = {
    connections.find(equal("_id", connectionId)).head().map[Connection]((doc: Document) => {
      documentToConnection(doc)
    })
  }

  override def update(connection: Connection): Future[UpdateResult] = {
    connections.replaceOne(equal("_id", connection._id), Document(Json.toJson(connection).toString)).head()
  }

  override def save(connection: Connection): Future[Completed] = {
    val connectionJson: String = Json.toJson(connection).toString
    val doc: Document = Document(connectionJson)
    connections.insertOne(doc).head()
  }

  override def findAll: Future[Seq[Connection]] = {
    val future: Future[Seq[Document]] = connections.find().toFuture()
    future.map { (docs: Seq[Document]) =>
      docs.map {
        doc: Document => documentToConnection(doc)
      }
    }
  }

  override def delete(connectionId: String): Future[DeleteResult] = {
    connections.deleteOne(equal("_id", connectionId)).head()
  }

  private def documentToConnection(doc: Document): Connection = {
    Connection(
      doc.get("_id").get.asString().getValue,
      doc.get("name").get.asString().getValue,
      doc.get("dbType").get.asString().getValue,
      doc.get("dbName").get.asString().getValue
    )
  }

}
