package com.despegar.demo.api

import com.despegar.demo.utils.LogSupport
import com.despegar.demo.BuildInfo
import org.http4s.HttpService
import org.http4s.dsl._

class HealthService extends LogSupport {

  def service(): HttpService = {
    def route = HttpService {
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
