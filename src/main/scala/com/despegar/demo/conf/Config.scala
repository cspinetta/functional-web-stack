package com.despegar.demo.conf

import com.despegar.demo.utils.ConfigurationSupport

sealed trait Config

object Config extends ConfigurationSupport {

  case class Datasource(url: String,
                        driverClassName: String,
                        username: String,
                        password: String,
                        maxPoolSize: Int = 20,
                        connectTimeout: Long = 4000,
                        socketTimeout: Long = 10000,
                        logEnabled: Boolean = true,
                        debugEnabled: Boolean = true) extends Config

  lazy val datasource: Datasource = pureconfig.loadConfigOrThrow[Datasource](config.getConfig("datasource"))
}


