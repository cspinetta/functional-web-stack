package com.despegar.demo.store

import java.time.LocalDate

import cats.effect.IO
import cats.implicits._
import com.despegar.demo.utils.LogSupport
import doobie._
import doobie.h2._
import doobie.implicits._

trait DBFixture extends LogSupport {

  val transactor: H2Transactor[IO] = H2Transactor.newH2Transactor[IO]("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", "").unsafeRunSync()

  def createSchema(): Unit = {
    log.debug("Creating Test schema on H2...")

    val createTestSchema: Update0 = sql"""create schema if not exists Test""".update

    val createAliasFunction: Update0 = sql"""CREATE ALIAS if not exists DATE for "com.despegar.demo.store.CustomFunctions.truncateDate" """.update

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

  def insertCompany(id: Long, name: String, staffCount: Int): IO[Int] =
    sql"""insert into Test.company(id, name, staff_count) values($id, $name, $staffCount)""".update.run.transact(transactor)

  def insertEmployee(id: Long, name: String, age: Option[Int], salary: BigDecimal, startDate: LocalDate, companyId: Long): IO[Int] =
    sql"""insert into Test.employee(id, name, age, salary, start_date, company_id)
            values($id, $name, $age, $salary, $startDate, $companyId)""".update.run.transact(transactor)
}

object CustomFunctions {
  def truncateDate(datetime: String): LocalDate = {
    import java.time.format.DateTimeFormatter
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    LocalDate.parse(datetime.substring(0, 10),formatter)
  }
}