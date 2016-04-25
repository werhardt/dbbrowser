package actions

import com.google.inject.{Inject, Singleton}
import models.User
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._
import services.{SessionService, UserService}

import scala.concurrent.Future
import scala.util.control.NonFatal

/**
  * Created by ismet on 21/12/15.
  */
class UserRequest[A](val user: User, request: Request[A]) extends WrappedRequest[A](request) {
  def sessionId = request.cookies.get("sessionId").get.value
}

@Singleton
class SecureAction @Inject()(sessionService: SessionService,
                             userService: UserService,
                            val messagesApi: MessagesApi) extends ActionBuilder[UserRequest] with ActionRefiner[Request, UserRequest] with I18nSupport  {

  override protected def refine[A](request: Request[A]): Future[Either[Result, UserRequest[A]]] = {

    request.cookies.get("sessionId").map { c: Cookie =>
      sessionService
        .findUserBySessionId(c.value)
        .map(u => Right(new UserRequest[A](u, request)))
        .recover {
          case NonFatal(_) => Left(Results.Forbidden(views.html.errors.forbidden()))
        }
    }.getOrElse(Future {
      Left(Results.Forbidden(views.html.errors.forbidden()))
    })

  }
}
