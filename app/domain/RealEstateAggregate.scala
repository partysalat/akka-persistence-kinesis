package domain

import akka.persistence.SnapshotMetadata
import domain.BaseAggregate.{GetState, KillAggregate}
import domain.RealEstateAggregate._

object RealEstateAggregate{
  import BaseAggregate._

  case class RealEstate(id:Long, published:Boolean) extends State
  // Commands
  case class Publish(exposeId:Long) extends Command
  case class Unpublish(exposeId:Long) extends Command

  //Events
  case class RealEstatePublished() extends Event
  case class RealEstateUnpublished() extends Event
}

class RealEstateAggregate(realEstateId:Long) extends BaseAggregate{
  override def persistenceId: String = realEstateId.toString
  state = RealEstate(realEstateId, published = false)

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
    case Publish =>
      persist(RealEstatePublished())(afterEventPersisted)
    case KillAggregate =>
      context.stop(self)
  }
  val published: Receive = {
    case GetState =>
      respond()
    case Unpublish =>
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
