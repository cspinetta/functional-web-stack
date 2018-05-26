package com.despegar.demo.store

import java.sql.Timestamp
import java.time.LocalDateTime

import com.despegar.demo.conf.ConfigSupport
import com.despegar.demo.utils.{LogSupport, SqlLogSupport}
import doobie._

// TODO - Ver
trait DemoStore extends LogSupport with SqlLogSupport with ConfigSupport {

  implicit val DateTimeMeta: Meta[LocalDateTime] = Meta[Timestamp].xmap(ts => ts.toLocalDateTime, dt => Timestamp.valueOf(dt))

}
