package services

import com.google.inject.Inject
import daos.{ConnectionDao, UserDao}
import models.Connection
import org.mongodb.scala.Completed
import org.mongodb.scala.result.UpdateResult

import scala.concurrent.Future


/**
  * Created by erh.
  */
class ConnectionService @Inject()(connectionDao: ConnectionDao) {

  def find(connectionId: String): Future[Connection] = connectionDao.find(connectionId)

  def update(connection: Connection): Future[UpdateResult] = connectionDao.update(connection)

  def save(connection: Connection): Future[Completed] = connectionDao.save(connection)

  def findAll = connectionDao.findAll

  def delete(connectionId: String) = connectionDao.delete(connectionId)

}
