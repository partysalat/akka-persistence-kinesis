package service

import akka.actor._
import domain.RealEstateAggregate

object RealEstateAggregateManager{

  import AggregateManager._

  case class PublishRealEstate(realEstateId: String) extends Command
  case class UnpublishRealEstate(realEstateId: String) extends Command
  case class GetRealEstate(realEstateId: String) extends Command

  def props: Props = Props(new RealEstateAggregateManager)
}

class RealEstateAggregateManager extends AggregateManager {

  import domain.RealEstateAggregate._
  import domain.BaseAggregate._
  import service.RealEstateAggregateManager._
  def processCommand = {
    case PublishRealEstate(realEstateId) =>
      val id = realEstateId
      processAggregateCommand(id, Publish())
    case UnpublishRealEstate(realEstateId) =>
      val id = realEstateId
      processAggregateCommand(id, Unpublish())
    case GetRealEstate(realEstateId) =>
//      val id = "user-" + name
      val id = realEstateId
      processAggregateCommand(id, GetState)
  }

  override def aggregateProps(id: String) = RealEstateAggregate.props(id)
}