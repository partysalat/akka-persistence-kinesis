package kinesis

import com.amazonaws.services.kinesis.clientlibrary.lib.worker.KinesisClientLibConfiguration

case class KinesisConfig(
                          kinesisClientLibConfiguration: KinesisClientLibConfiguration,
                          enabled: Boolean
                        )