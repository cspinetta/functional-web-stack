package com.despegar.demo.program

import java.time.LocalDate

import com.despegar.demo.model.Employee
import com.despegar.demo.store.{CompanyStore, DBFixture, EmployeeStore}
import com.despegar.demo.utils.LogSupport
import doobie._
import doobie.implicits._
import org.scalatest.{BeforeAndAfter, Matchers, WordSpecLike}


class CompanyProgramIT extends WordSpecLike with Matchers with BeforeAndAfter with DBFixture with LogSupport {

  before {
    createSchema(transactor)
  }

  "The Company Program" should {
    "hire an employee" in {
      val instance = new CompanyProgram(new CompanyStore, new EmployeeStore)

      val companyId = 9L
      val employee = Employee(id = None, name = "Trini", age = Some(34), salary = BigDecimal.valueOf(30000), startDate = LocalDate.now().minusYears(1))
      val program: ConnectionIO[Long] = instance.hire(companyId, employee)
      val newEmployeeId = program.transact(transactor).unsafeRunSync()
      newEmployeeId.toInt should be > 0
    }
  }
}
