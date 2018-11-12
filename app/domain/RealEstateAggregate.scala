package domain

import akka.actor.Props
import akka.persistence.SnapshotMetadata
import domain.BaseAggregate.{GetState, KillAggregate}
import domain.RealEstateAggregate._
import play.api.libs.json.{Format, Json, OFormat}

object RealEstateAggregate{
  import BaseAggregate._

  //State
  case class RealEstate(id:Long, published:Boolean) extends State
  implicit val realEstateFormat: Format[RealEstate] = Json.format[RealEstate]

  // Commands
  case class Publish() extends Command
  case class Unpublish() extends Command

  //Events
  case class RealEstatePublished() extends Event
  case class RealEstateUnpublished() extends Event

  def props(realEstateId: String): Props = Props(new RealEstateAggregate(realEstateId))
}

class RealEstateAggregate(realEstateId:String) extends BaseAggregate{
  override def persistenceId: String = realEstateId.toString
  state = RealEstate(realEstateId.toLong, published = false)

  override def updateState(evt: BaseAggregate.Event): Unit = evt match {
    case RealEstatePublished() =>
      context.become(published)
      setStateToPublished(true)
    case RealEstateUnpublished() =>
      context.become(unpublished)
      setStateToPublished(false)
  }

  private def setStateToPublished(published:Boolean): Unit = {
    state match {
      case s: RealEstate => state = s.copy(published = published)
      case _ => //nothing
    }
  }

  val unpublished: Receive = {
    case GetState =>
      respond()
    case Publish() =>
      persist(RealEstatePublished())(afterEventPersisted)
    case KillAggregate =>
      context.stop(self)
  }
  val published: Receive = {
    case GetState =>
      respond()
    case Unpublish() =>
      persist(RealEstateUnpublished())(afterEventPersisted)

    case KillAggregate =>
      context.stop(self)
  }
  override def receiveCommand: Receive = unpublished
  override protected def restoreFromSnapshot(metadata: SnapshotMetadata, state: BaseAggregate.State): Unit ={
    this.state = state
    state match {
      case realEstate: RealEstate if realEstate.published => context become published
      case realEstate: RealEstate if !realEstate.published => context become unpublished
    }
  }


}
