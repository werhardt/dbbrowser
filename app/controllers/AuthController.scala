package controllers

import java.util.UUID

import actions.SecureAction
import com.github.t3hnar.bcrypt._
import com.google.inject.Inject
import com.mongodb.MongoWriteException
import forms.AuthForms.{LoginData, SignupData}
import models.{Session, User}
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.{SessionService, UserService}

import scala.concurrent.Future

/**
  * Created by ismet on 13/12/15.
  */
class AuthController @Inject()(userService: UserService,
                               sessionService: SessionService,
                               secureAction: SecureAction,
                               val messagesApi: MessagesApi) extends Controller with I18nSupport {

  def signup = Action.async { implicit request =>
    val rawBody: JsValue = request.body.asJson.get
    try {
      val signupData: SignupData = rawBody.validate[SignupData].get
      val user = User(
        UUID.randomUUID().toString,
        signupData.name,
        signupData.email,
        signupData.username,
        signupData.password.bcrypt,
        System.currentTimeMillis())

      userService.save(user).map((_) => {
        Ok
      }).recoverWith {
        case e: MongoWriteException => Future {
          Forbidden
        }
        case _ => Future {
          Forbidden
        }
      }
    } catch {
      case e: Exception => Future {
        BadRequest
      }
    }
  }

  def login = Action { implicit request =>
    Ok(views.html.login())
  }

  def doLogin = Action.async { implicit request =>
//    val rawBody: JsValue = request.body.asJson.get
    val username = request.body.asFormUrlEncoded.get("username").head
    val password = request.body.asFormUrlEncoded.get("password").head

    try {
//      val loginData = rawBody.validate[LoginData].get
      val loginData = LoginData(username, password)

      userService.findByUsername(loginData.username).map((user: User) => {
        if (loginData.password.isBcrypted(user.password)) {
          val sessionId: String = UUID.randomUUID().toString
          val currentTimeMillis: Long = System.currentTimeMillis()
          val session: Session = models.Session(
            sessionId,
            user._id,
            request.remoteAddress,
            request.headers.get("User-Agent").get,
            currentTimeMillis,
            currentTimeMillis
          )
          sessionService.save(session)
//          val response = Map("sessionId" -> sessionId)
//          Ok(Json.toJson(response)).withCookies(Cookie("sessionId", sessionId))
//          Redirect("/connections").withCookies(Cookie("sessionId", sessionId))
          Redirect(routes.ConnectionController.listConnections()).withCookies(Cookie("sessionId", sessionId))

        } else {
          Redirect(routes.AuthController.login()).flashing("error" -> Messages("login.wrong_password"))
        }
      }).recoverWith {
        case e: IllegalStateException => Future {
          Redirect(routes.AuthController.login()).flashing("error" -> Messages("login.unkown_username"))
        }
      }
    } catch {
      case e: Exception => Future {
        BadRequest
      }
    }
  }

  def securedSampleAction = secureAction { implicit request =>
    Ok
  }

  def logout = secureAction { implicit request =>
    sessionService.delete(request.sessionId)
    Redirect(routes.AuthController.login()).discardingCookies(DiscardingCookie("sessionId"))
  }


}
