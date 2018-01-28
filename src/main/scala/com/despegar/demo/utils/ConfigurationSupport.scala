package com.despegar.demo.utils


import java.io.File

import com.typesafe.config.ConfigFactory

import scala.concurrent.duration.FiniteDuration

trait ConfigurationSupport {
  val config = ConfigurationSupport.configuration
  val currentEnvironment: String = Option(System.getProperty(ConfigurationSupport.ENVIRONMENT_KEY)).getOrElse(config.getString(ConfigurationSupport.ENVIRONMENT_KEY))
}

object ConfigurationSupport extends LogSupport {
  val ENVIRONMENT_KEY = "environment"

  implicit def asFiniteDuration(d: java.time.Duration): FiniteDuration = scala.concurrent.duration.Duration.fromNanos(d.toNanos)

  val configuration: com.typesafe.config.Config = {
    val defaultConfig = ConfigFactory.load()
    val overrideFile = new File(Option(System.getProperty("environmentOverride")).getOrElse("environment-override.conf"))
    val environment = Option(System.getProperty(ENVIRONMENT_KEY)).getOrElse(defaultConfig.getString(ENVIRONMENT_KEY))

    log.info(s"Loading config from [$environment] overriding with [$overrideFile]")

    ConfigFactory.parseFile(overrideFile)
      .withFallback(defaultConfig.getConfig(environment)).withFallback(defaultConfig)
  }

}