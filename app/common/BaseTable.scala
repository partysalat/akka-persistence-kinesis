package common

import java.sql.Date

import org.joda.time.DateTime
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

trait BaseTable{
  self: HasDatabaseConfigProvider[JdbcProfile] =>
  import profile.api._

  implicit def dateTime =
    MappedColumnType.base[DateTime, Date](
      dateTime => new Date(dateTime.getMillis),
      date => new DateTime(date.getTime)
    )
}
