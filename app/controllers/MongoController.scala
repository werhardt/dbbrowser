package controllers

import javax.inject._

import play.api.data._
import play.api.data.Forms._
import actions.SecureAction
import org.mongodb.scala.Document
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json._
import play.api.mvc._
import services.Mongo

import scala.concurrent.ExecutionContext

/**
  * Created by erh on 18.04.16.
  */
class MongoController @Inject()(mongo: Mongo,
                                     secureAction: SecureAction,
                                     val messagesApi: MessagesApi)(implicit exec: ExecutionContext) extends Controller with I18nSupport {




  def listDbs() = secureAction.async { implicit request =>

    val futureDbs = mongo.listDatabases()

    futureDbs.map { dbs =>
      Ok(views.html.internal.dbs(dbs.toList.sorted))
    }
  }

  def listCollections(db: String) = secureAction.async {

    val futureCollections = mongo.listCollections(db)

    futureCollections.map { collections =>
      Ok(views.html.internal.collections(collections.toList.sorted, db))
    }
  }

  def listDocuments(db: String, collection: String) = secureAction.async { implicit request =>


    val form = filterForm.bindFromRequest.get

    val filter = buildFilter(form)

    val futureDocuments = mongo.listDocuments(db, collection, filter)

    futureDocuments.map { documents =>
      Ok(views.html.internal.documents(documents.toList, db, collection, filterForm.fill(FilterData(form.filter)) ))
    }
  }

  def buildFilter(form: FilterData): Document = {
    if(form.filter.isEmpty)
      Document()
    else {
      Document.apply("{" + form.filter.get +"}")
//      Document.apply("{\"firstname\": \"Florian\"}")
    }
  }

  val filterForm = Form(
    mapping(
      "filter" -> optional(text)
    )(FilterData.apply)(FilterData.unapply)
  )
}

case class FilterData(filter: Option[String])