package common

import javax.inject._
import play.api.mvc._

import scala.concurrent.ExecutionContext

@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents, @Named("camera") cameria:String) extends BaseController {
  implicit val ec: ExecutionContext = controllerComponents.executionContext

  def index = Action {
    Ok(views.html.index(s"Your new application is ready.$cameria"))
  }

}
