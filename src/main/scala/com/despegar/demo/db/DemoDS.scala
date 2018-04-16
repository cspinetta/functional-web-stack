package com.despegar.demo.db

import cats.effect.IO
import com.despegar.demo.conf.ConfigSupport
import com.despegar.demo.utils.ThreadUtils
import com.zaxxer.hikari.HikariDataSource
import doobie.util.transactor.Transactor

object DemoDS extends ConfigSupport {
  private[this] def dataSource: HikariDataSource = {
    val ds = new HikariDataSource
    ds.setPoolName("Demo-Hikari-Pool")
    ds.setMaximumPoolSize(config.datasource.maxPoolSize)
    ds.setDriverClassName(config.datasource.driverClassName)
    ds.setJdbcUrl(config.datasource.url)
    ds.addDataSourceProperty("user", config.datasource.username)
    ds.addDataSourceProperty("password", config.datasource.password)
    ds.addDataSourceProperty("connectTimeout", config.datasource.connectTimeout)
    ds.addDataSourceProperty("socketTimeout", config.datasource.socketTimeout)
    ds.setThreadFactory(ThreadUtils.namedThreadFactory("demo-hikari-pool"))
    ds
  }

  lazy val DemoTransactor: Transactor[IO] = {
    Transactor.fromDataSource[IO](dataSource)
  }
}
