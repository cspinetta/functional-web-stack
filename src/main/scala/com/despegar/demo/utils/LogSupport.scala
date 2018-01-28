package com.despegar.demo.utils

import com.despegar.demo.conf.Config
import doobie.util.log.{ExecFailure, ProcessingFailure, Success}
import org.slf4j.{Logger, LoggerFactory}

trait LogSupport {
  val log: Logger = LoggerFactory.getLogger(this.getClass)
}

trait SqlLogSupport { self: LogSupport =>

  import doobie.imports

  private val debugEnabled = Config.datasource.debugEnabled

  implicit val han: imports.LogHandler = imports.LogHandler {
    case Success(s, a, e1, e2) =>
      if (debugEnabled)
        log.debug(s"""Successful Statement Execution:
                     |
            |  ${s.lines.dropWhile(_.trim.isEmpty).mkString("\n  ")}
                     |
            | arguments = [${a.mkString(", ")}]
                     |   elapsed = ${e1.toMillis} ms exec + ${e2.toMillis} ms processing (${(e1 + e2).toMillis} ms total)
          """.stripMargin)

    case ProcessingFailure(s, a, e1, e2, t) =>
      log.error(s"""Failed ResultSet Processing:
                   |
          |  ${s.lines.dropWhile(_.trim.isEmpty).mkString("\n  ")}
                   |
          | arguments = [${a.mkString(", ")}]
                   |   elapsed = ${e1.toMillis} ms exec + ${e2.toMillis} ms processing (failed) (${(e1 + e2).toMillis} ms total)
                   |   failure = ${t.getMessage}
        """.stripMargin)

    case ExecFailure(sql, args, elapsed, exc) =>
      log.error(s"""Failed Statement Execution:
                   |
          |  ${sql.lines.dropWhile(_.trim.isEmpty).mkString("\n  ")}
                   |
          | arguments = [${args.mkString(", ")}]
                   |   elapsed = ${elapsed.toMillis} ms exec (failed)
                   |   failure = ${exc.getMessage}
        """.stripMargin, exc)
  }
}
