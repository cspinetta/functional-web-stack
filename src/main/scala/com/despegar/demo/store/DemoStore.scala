package com.despegar.demo.store

import java.sql.Timestamp
import java.time.LocalDateTime

import com.despegar.demo.utils.{LogSupport, SqlLogSupport}
import com.despegar.demo.utils.SqlLogSupport
import doobie.imports.{ConnectionIO, HC, Transactor, _}
import fs2.Task
import fs2.interop.cats._

// TODO - Ver
trait DemoStore extends LogSupport with SqlLogSupport {

  def delay[A](f: () => A): ConnectionIO[A] = HC.delay(f())

  def logBracket[A](before: String, after: String, logF: (String) => Unit = log.debug)(action: ConnectionIO[A]): ConnectionIO[A] = {
    import cats.syntax.cartesian._
    delay(() => logF(before)) *> action <* delay(() => logF(after))
  }

  implicit val DateTimeMeta: Meta[LocalDateTime] = Meta[Timestamp].xmap(ts => ts.toLocalDateTime, dt => Timestamp.valueOf(dt))

}

object DemoStore extends DemoStore {

  object syntax {
    implicit class PimpedConnectionIO[A](val connectionIO: ConnectionIO[A]) extends AnyVal {
      def runStore(tx: Transactor[Task]): Either[Throwable, A] = {
        connectionIO.transact(tx).unsafeAttemptRun()
      }
    }
  }
}