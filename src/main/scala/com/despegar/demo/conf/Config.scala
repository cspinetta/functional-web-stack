package com.despegar.demo.conf

import scala.concurrent.duration.FiniteDuration

trait ConfigSupport {
  protected lazy val config: Config.AppConfig = Config.loadConfig
}

object Config extends ConfigLoader {

  def loadConfig: Config.AppConfig = pureconfig.loadConfigOrThrow[Config.AppConfig](config)

  case class AppConfig(db: DB, client: Client)

  case class DB(useDemo: Boolean, debugEnabled: Boolean, datasource: Datasource)

  case class Datasource(url: String,
                        driverClassName: String,
                        username: String,
                        password: String,
                        maxPoolSize: Int = 20,
                        connectTimeout: Long = 4000,
                        socketTimeout: Long = 10000)

  case class Client(maxTotalConnections: Int, idleTimeout: FiniteDuration, requestTimeout: FiniteDuration)

  lazy val datasource: Datasource = pureconfig.loadConfigOrThrow[Datasource](config.getConfig("db"))
  lazy val client: Client = pureconfig.loadConfigOrThrow[Client](config.getConfig("client"))
}
