package services

//  val db

import com.google.inject.{Inject, Singleton}
import org.mongodb.scala.{Document, MongoClient, MongoDatabase}
import play.Logger
import play.api.Configuration
import play.api.inject.ApplicationLifecycle

import scala.concurrent.Future

/**
  * Created by ismet on 07/12/15.
  */
@Singleton
class Mongo @Inject()(applicationLifecycle: ApplicationLifecycle, configuration: Configuration) {

  val client: MongoClient = MongoClient()

  private val dbName: String = configuration.getString("mongo.db.name").get

  val db: MongoDatabase = client.getDatabase(dbName)

  applicationLifecycle.addStopHook(() => {
    Logger.warn("Closing Mongo connection")
    Future.successful(client.close())
  })


  def listDatabases() = client.listDatabaseNames().toFuture()

  def listCollections(db: String) = client.getDatabase(db).listCollectionNames().toFuture()

  def listDocuments(db: String, collection: String,  filter: Document = Document()) =
    client.getDatabase(db).getCollection(collection).find(filter).toFuture()

}
