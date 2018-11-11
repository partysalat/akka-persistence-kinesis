package kinesis

import java.time.temporal.TemporalUnit
import java.time.{Clock, LocalDate, ZoneId}
import java.util.Date

import com.amazonaws.auth.{AWSCredentialsProviderChain, DefaultAWSCredentialsProviderChain}
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.{InitialPositionInStream, KinesisClientLibConfiguration}
import com.google.inject.{AbstractModule, Provides, Singleton}
import kinesis.sps.SpsKinesisConsumer

class KinesisModule extends AbstractModule {
  override def configure() = {
    bind(classOf[SpsKinesisConsumer]).asEagerSingleton()
  }

  @Provides
  def getKinesisClientLibConfig: KinesisConfig = {
    val yesterday = LocalDate.now().minusDays(1)
    val date = Date.from(yesterday.atStartOfDay(ZoneId.systemDefault).toInstant)

    val kinesisClientLibConfiguration = new KinesisClientLibConfiguration(
      "ppaSps",
      "myStream",
      new DefaultAWSCredentialsProviderChain,
      "kinesisWorker"
    )
      .withRegionName("eu-central-1")
      .withCallProcessRecordsEvenForEmptyRecordList(true)
      .withInitialPositionInStream(InitialPositionInStream.TRIM_HORIZON)
//      .withTimestampAtInitialPositionInStream(date)


    KinesisConfig(
      kinesisClientLibConfiguration,
      enabled = true
    )
  }


}
