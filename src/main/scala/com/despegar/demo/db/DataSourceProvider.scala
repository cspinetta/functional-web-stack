package com.despegar.demo.db

import cats.effect.IO
import cats.implicits._
import com.despegar.demo.conf.ConfigSupport
import com.despegar.demo.utils.{LogSupport, ThreadUtils}
import com.zaxxer.hikari.HikariDataSource
import doobie._
import doobie.h2._
import doobie.implicits._
import doobie.util.transactor.Transactor

object DataSourceProvider extends ConfigSupport {
  private[this] def dataSource: HikariDataSource = {
    val ds = new HikariDataSource
    ds.setPoolName("Demo-Hikari-Pool")
    ds.setMaximumPoolSize(config.db.datasource.maxPoolSize)
    ds.setDriverClassName(config.db.datasource.driverClassName)
    ds.setJdbcUrl(config.db.datasource.url)
    ds.addDataSourceProperty("user", config.db.datasource.username)
    ds.addDataSourceProperty("password", config.db.datasource.password)
    ds.addDataSourceProperty("connectTimeout", config.db.datasource.connectTimeout)
    ds.addDataSourceProperty("socketTimeout", config.db.datasource.socketTimeout)
    ds.setThreadFactory(ThreadUtils.namedThreadFactory("demo-hikari-pool"))
    ds
  }

  lazy val DSTransactor: Transactor[IO] = {
    Transactor.fromDataSource[IO](dataSource)
  }

  lazy val H2TransactorInstance: H2Transactor[IO] = H2Transactor.newH2Transactor[IO]("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", "").unsafeRunSync()
}

trait DemoSchema extends LogSupport {

  def createSchema(transactor: Transactor[IO]): Unit = {
    log.debug("Creating Test schema on H2...")

    val createTestSchema: Update0 = sql"""create schema if not exists Test""".update

    val createAliasFunction: Update0 = sql"""CREATE ALIAS if not exists DATE for "com.despegar.demo.utils.StoreUtils.truncateDate" """.update

    val dropCompanyTable: Update0 = sql"""drop table if exists Test.company""".update

    val createCompanyTable: Update0 =
      sql"""
         CREATE TABLE Test.company (
           id int(11) primary key auto_increment not null,
           name varchar(255) NOT NULL,
           staff_count int(3) NOT NULL DEFAULT 0
         )
      """.update


    val dropEmployeeTable: Update0 =
      sql"""drop table if exists Test.employee""".update

    val createEmployeeTable: Update0 =
      sql"""
         CREATE TABLE Test.employee (
           id int(11) primary key auto_increment not null,
           name varchar(255) NOT NULL,
           age int(11) DEFAULT NULL,
           salary decimal(20, 2) not null,
           start_date DATE not null,
           company_id int(11) not null
         )
      """.update

    (createTestSchema.run *> createAliasFunction.run *> dropCompanyTable.run *>
      createCompanyTable.run *> dropEmployeeTable.run *> createEmployeeTable.run)
      .transact[IO](transactor).unsafeRunSync()


    log.debug("Test schema created on h2")
  }
}

object DemoSchema extends DemoSchema
