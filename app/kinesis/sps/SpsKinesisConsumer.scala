package kinesis.sps

import akka.NotUsed
import akka.actor.{ActorRef, ActorSystem}
import akka.stream.scaladsl.Flow
import com.google.inject.name.Named
import com.google.inject.{Inject, Singleton}
import kinesis.{KinesisConfig, KinesisConsumer, KinesisMessageMeta}
import play.api.Logger
import play.api.libs.json.{Format, Json}
import service.RealEstateAggregateManager.{PublishRealEstate, UnpublishRealEstate}

import scala.concurrent.Future

object EntitlementCommand{
  implicit val ContactRequestsCategoryMappingFormat: Format[EntitlementCommand] =
    Json.format[EntitlementCommand]
}
case class EntitlementCommand(id:String, publish :Boolean)

@Singleton
class SpsKinesisConsumer @Inject()(
                                    val config:KinesisConfig,
                                    @Named("realestateAggregateManager") val realEstateAggregateManager:ActorRef
                                  )(implicit val actorSystem: ActorSystem)
  extends KinesisConsumer[EntitlementCommand]{
  val logger = Logger(this.getClass)



  override def flow: Flow[(KinesisMessageMeta, Option[EntitlementCommand]), KinesisMessageMeta, NotUsed] =
    Flow[(KinesisMessageMeta, Option[EntitlementCommand])]
    .map(validate)
      .mapAsync(1)(handleMessage)

  def handleMessage(msg:(KinesisMessageMeta, Option[EntitlementCommand]))= {
    logger.info(msg._2.toString)
    if(msg._2.isEmpty){
      Future.successful(msg._1)
    }else{
      val entitlementCommand = msg._2.get

      if (entitlementCommand.publish) {
        realEstateAggregateManager ! PublishRealEstate(entitlementCommand.id)
      } else {
        realEstateAggregateManager ! UnpublishRealEstate(entitlementCommand.id)
      }

      Future.successful(msg._1)
    }
  }
  def validate(msg:(KinesisMessageMeta, Option[EntitlementCommand]))= {
    if(msg._2.isEmpty){
      logger.error("Handle error case")
    }
    (msg._1, msg._2)
  }

}
