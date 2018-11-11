import com.google.inject.{AbstractModule, Provides, Singleton}
import java.time.Clock

import com.google.inject.name.Named
import com.typesafe.config.{Config, ConfigFactory}

class Module extends AbstractModule {

  override def configure() = {
    bind(classOf[Clock]).toInstance(Clock.systemDefaultZone)
  }

  @Provides
  @Singleton
  @Named("camera")
  def getGdprResultsBucketName(config: Config): String = {
    config.getString("camera.filename")
  }

}
