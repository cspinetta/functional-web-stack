package com.despegar.demo.api

import cats.effect.IO
import com.despegar.demo.BuildInfo
import com.despegar.demo.utils.LogSupport
import org.http4s.HttpService
import org.http4s.dsl.io._

class HealthService extends LogSupport {

  def service(): HttpService[IO] = {
    def route = HttpService[IO] {
      case GET -> Root / "health-check" => Ok(fullVersion)
    }
    route
  }

  private def fullVersion: String = {
    val version = s"${BuildInfo.name}:${BuildInfo.version}"
    log.info(s"Demo health-check: $version")
    version
  }
}

object HealthService {
  def apply(): HealthService = new HealthService()
}
