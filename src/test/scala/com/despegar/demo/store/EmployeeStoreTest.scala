package com.despegar.demo.store

import java.time.LocalDate

import com.despegar.demo.api.EmployeeFilter
import com.despegar.demo.db.DemoDS
import com.despegar.demo.model.Employee
import doobie._
import doobie.implicits._
import org.scalatest.{FunSuite, Matchers}

class EmployeeStoreTest extends FunSuite with Matchers {

  test("findAll ok") {
    val instance = new EmployeeStore
    val program: ConnectionIO[List[Employee]] = instance.findAll
    val employees = program.transact(DemoDS.DemoTransactor).unsafeRunSync()

    employees.size shouldBe 2
    employees.map(e => e.name).contains("Pepe") shouldBe true
    employees.map(e => e.name).contains("Maria") shouldBe true
  }

  test("findByFilter") {
    val instance = new EmployeeStore
    val filter = EmployeeFilter(
      ids = List(6L, 9L),
      name = None,
      minimumAge = Some(22),
      startDateFrom = LocalDate.now().minusYears(5),
      startDateTo = LocalDate.now().minusMonths(1),
      offset = None
    )
    val program: ConnectionIO[List[Employee]] = instance.findByFilter(filter)

    val employees = program.transact(DemoDS.DemoTransactor).unsafeRunSync()
    employees.size shouldBe 1
    employees.head.name shouldBe "Pepe"
  }

  test("happy birthday") {
    val instance = new EmployeeStore
    val employeeId = 6L
    val program: ConnectionIO[Int] = instance.happyBirthday(employeeId)
    val updatedRows = program.transact(DemoDS.DemoTransactor).unsafeRunSync()

    updatedRows shouldBe 1
  }

}
