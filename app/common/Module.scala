package common

import java.time.Clock

import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport
import service.RealEstateAggregateManager

object Module{
  val REALESTATE_AGGREGATE_MANAGER = "realestateAggregateManager"
}
class Module extends AbstractModule with AkkaGuiceSupport {

  override def configure() = {
    bind(classOf[Clock]).toInstance(Clock.systemDefaultZone)
    bindActor[RealEstateAggregateManager](Module.REALESTATE_AGGREGATE_MANAGER)
  }

}
