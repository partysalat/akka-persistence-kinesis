package common

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import com.google.inject.name.Named
import domain.RealEstateAggregate.RealEstate
import javax.inject._
import play.api.libs.json.Json
import play.api.mvc._
import service.RealEstateAggregateManager.GetRealEstate

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

@Singleton
class HomeController @Inject()(
                                val controllerComponents: ControllerComponents,
                                @Named(Module.REALESTATE_AGGREGATE_MANAGER) val realEstateAggregateManager: ActorRef)
  extends BaseController {
  implicit val ec: ExecutionContext = controllerComponents.executionContext

  implicit val timeout = Timeout(2 seconds)


  def index = Action {
    Ok(views.html.index(s"Your new application is ready."))
  }

  def getRealEstate(id: String) = Action.async {
    (realEstateAggregateManager ? GetRealEstate(id))
      .map{
        case realEstate:RealEstate => Ok(Json.toJson(realEstate))
      }

  }

}
