package com.despegar.demo.store

import java.time.LocalDate

import cats.effect.IO
import com.despegar.demo.db.DemoSchema
import com.despegar.demo.utils.LogSupport
import doobie.h2._
import doobie.implicits._

trait DBFixture extends DemoSchema with LogSupport {

  val transactor: H2Transactor[IO] = H2Transactor.newH2Transactor[IO]("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", "").unsafeRunSync()

  def insertCompany(id: Long, name: String, staffCount: Int): IO[Int] =
    sql"""insert into Test.company(id, name, staff_count) values($id, $name, $staffCount)""".update.run.transact(transactor)

  def insertEmployee(id: Long, name: String, age: Option[Int], salary: BigDecimal, startDate: LocalDate, companyId: Long): IO[Int] =
    sql"""insert into Test.employee(id, name, age, salary, start_date, company_id)
            values($id, $name, $age, $salary, $startDate, $companyId)""".update.run.transact(transactor)
}
