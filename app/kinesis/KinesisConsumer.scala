package kinesis

import akka.actor.ActorSystem
import akka.{Done, NotUsed}
import akka.stream.ActorAttributes.supervisionStrategy
import akka.stream.{ActorMaterializer, ActorMaterializerSettings, Supervision}
import akka.stream.scaladsl.{Flow, Sink, Source}
import com.contxt.kinesis.{KinesisRecord, KinesisSource}
import play.api.Logger
import play.api.libs.json.{Json, Reads}

import scala.concurrent.Future
import scala.util.control.NonFatal

case class KinesisMessageMeta(markProcessed: () => Unit, sequenceNumber: String)

abstract class KinesisConsumer[T: Reads] {
  val logger: Logger

  val config: KinesisConfig

  def flow:Flow[(KinesisMessageMeta, Option[T]), KinesisMessageMeta, NotUsed]

  implicit val actorSystem: ActorSystem

  val decider: Supervision.Decider = {
    case NonFatal(e) =>
      logger.error("Error on message processing", e)
      Supervision.Resume
    case t =>
      logger.error("Fatal error on message processing", t)
      Supervision.Stop
  }
  implicit val materializer =
    ActorMaterializer(
      ActorMaterializerSettings(actorSystem)
        .withSupervisionStrategy(decider))

  private def parseJson(record: KinesisRecord) = {
    logger.info(s"parse json ${record.data.utf8String}")
    Json.fromJson[T](Json.parse(record.data.utf8String)).asOpt
  }

  private def recordToTuple(record: KinesisRecord) = {
    (KinesisMessageMeta(record.markProcessed, record.sequenceNumber), parseJson(record))

  }

  val source: Source[(KinesisMessageMeta, Option[T]), Future[Done]] = KinesisSource(config.kinesisClientLibConfiguration)
    .map(recordToTuple)


  if (config.enabled) {
    source
      .via(flow.withAttributes(supervisionStrategy(decider)))
      .runWith(Sink.foreach[KinesisMessageMeta](messageMeta =>{
        logger.info(s"ack message meta with sequence ${messageMeta.sequenceNumber}")
        messageMeta.markProcessed()
      }))
  }
}
