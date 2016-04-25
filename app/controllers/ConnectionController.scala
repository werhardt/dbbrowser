package controllers

import java.util.UUID
import javax.inject._

import actions.SecureAction
import forms.ConnectionForm
import forms.ConnectionForm.ConnectionData
import models.Connection
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc._
import services.{ConnectionService, Mongo}

import scala.concurrent.ExecutionContext
/**
  * Created by erh on 18.04.16.
  */
class ConnectionController @Inject()(mongo: Mongo,
                                     secureAction: SecureAction,
                                     connectionService: ConnectionService,
                                     val messagesApi: MessagesApi)(implicit exec: ExecutionContext) extends Controller with I18nSupport {




  def listConnections = secureAction.async { implicit request =>

    val futureConnections = connectionService.findAll

    futureConnections.map { connections =>
      Ok(views.html.internal.connections(connections.toList))
    }
  }

  def addConnection() = secureAction { implicit request =>
    Ok(views.html.internal.connectionForm(ConnectionForm.connectionForm))
  }


  def saveConnection() = secureAction.async { implicit request =>

    val form = ConnectionForm.connectionForm.bindFromRequest.get

    val futureResult = if(form.id.isEmpty) {
      connectionService.save(Connection(UUID.randomUUID().toString, form.name, form.dbType, form.dbName))
    } else {
      connectionService.update(Connection(form.id.get, form.name, form.dbType, form.dbName))
    }

    futureResult.map { result =>
      Redirect(routes.ConnectionController.listConnections()).flashing("success" -> Messages("views.connections.save.success"))
    }
  }

  def editConnection(id: String) = secureAction.async { implicit request =>

    val futureConnection = connectionService.find(id)

    futureConnection.map { connection =>
      val form = ConnectionForm.connectionForm.fill(ConnectionData(Option(connection._id), connection.name, connection.dbType, connection.dbName))
      Ok(views.html.internal.connectionForm(form))
    }
  }

  def deleteConnection(id: String) = secureAction.async {

    val futureResult = connectionService.delete(id)

    futureResult.map { result =>
      Redirect(routes.ConnectionController.listConnections()).flashing("success" -> Messages("views.connections.delete.success"))
    }
  }
}
