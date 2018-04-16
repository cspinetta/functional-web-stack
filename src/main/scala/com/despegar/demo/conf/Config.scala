package com.despegar.demo.conf

import scala.concurrent.duration.FiniteDuration

trait ConfigSupport {
  protected val config: Config = Config
}

sealed trait Config {

  def currentEnvironment: String

  def datasource: Datasource
  def client: Client

  case class Datasource(url: String,
                        driverClassName: String,
                        username: String,
                        password: String,
                        maxPoolSize: Int = 20,
                        connectTimeout: Long = 4000,
                        socketTimeout: Long = 10000,
                        logEnabled: Boolean = true,
                        debugEnabled: Boolean = true)

  case class Client(maxTotalConnections: Int, idleTimeout: FiniteDuration, requestTimeout: FiniteDuration)
}

object Config extends Config with ConfigLoader {

  lazy val datasource: Datasource = pureconfig.loadConfigOrThrow[Datasource](config.getConfig("datasource"))
  lazy val client: Client = pureconfig.loadConfigOrThrow[Client](config.getConfig("client"))
}
