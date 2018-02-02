package com.despegar.demo.db

import cats.effect.IO
import com.despegar.demo.conf.Config
import com.despegar.demo.utils.ThreadUtils
import com.zaxxer.hikari.HikariDataSource
import doobie.util.transactor.Transactor
import doobie.util.transactor.Transactor.Aux

object DemoDS {
  private[this] def dataSource: HikariDataSource = {
    val ds = new HikariDataSource
    ds.setPoolName("Demo-Hikari-Pool")
    ds.setMaximumPoolSize(Config.datasource.maxPoolSize)
    ds.setDriverClassName(Config.datasource.driverClassName)
    ds.setJdbcUrl(Config.datasource.url)
    ds.addDataSourceProperty("user", Config.datasource.username)
    ds.addDataSourceProperty("password", Config.datasource.password)
    ds.addDataSourceProperty("connectTimeout", Config.datasource.connectTimeout)
    ds.addDataSourceProperty("socketTimeout", Config.datasource.socketTimeout)
    ds.setThreadFactory(ThreadUtils.namedThreadFactory("demo-hikari-pool"))
    ds
  }

  lazy val DemoTransactor: Aux[IO, HikariDataSource] = {
    Transactor.fromDataSource[IO](dataSource)
  }
}
