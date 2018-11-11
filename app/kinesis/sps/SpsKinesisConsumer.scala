package kinesis.sps

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.Flow
import com.google.inject.{Inject, Singleton}
import kinesis.{KinesisConfig, KinesisConsumer, KinesisMessageMeta}
import play.api.Logger
import play.api.libs.json.{Format, Json}

import scala.concurrent.Future

object EntitlementCommand{
  implicit val ContactRequestsCategoryMappingFormat: Format[EntitlementCommand] =
    Json.format[EntitlementCommand]
}
case class EntitlementCommand(foo:String)

@Singleton
class SpsKinesisConsumer @Inject()(val config:KinesisConfig)(implicit val actorSystem: ActorSystem)
  extends KinesisConsumer[EntitlementCommand]{
  val logger = Logger(this.getClass)

  def handleMessage(msg:(KinesisMessageMeta, Option[EntitlementCommand]))= {
    logger.info(msg._2.toString)
    Future.successful(msg._1)
  }

  override def flow: Flow[(KinesisMessageMeta, Option[EntitlementCommand]), KinesisMessageMeta, NotUsed] =
    Flow[(KinesisMessageMeta, Option[EntitlementCommand])]
      .mapAsync(1)(handleMessage)

}
