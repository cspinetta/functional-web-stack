package com.despegar.demo.program

import java.time.LocalDate

import com.despegar.demo.db.DemoDS
import com.despegar.demo.model.Employee
import com.despegar.demo.store.{CompanyStore, EmployeeStore}
import org.scalatest.{FunSuite, Matchers}
import doobie._
import doobie.implicits._


class CompanyProgramTest extends FunSuite with Matchers {

  test("hire") {
    val instance = new CompanyProgram(new CompanyStore, new EmployeeStore)

    val companyId = 9L
    val employee = Employee(id = None, name = "Trini", age = Some(34), salary = BigDecimal.valueOf(30000), startDate = LocalDate.now().minusYears(1))
    val program: ConnectionIO[Long] = instance.hire(companyId, employee)
    val newEmployeeId = program.transact(DemoDS.DemoTransactor).unsafeRunSync()
    System.out.println(s"New employee id: $newEmployeeId")
  }
}
