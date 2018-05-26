package com.despegar.demo.store

import java.time.LocalDate

import cats.implicits._
import com.despegar.demo.api.EmployeeFilter
import com.despegar.demo.model.Employee
import doobie._
import doobie.implicits._
import org.scalatest.{BeforeAndAfter, Matchers, WordSpecLike}

class EmployeeStoreIT extends WordSpecLike with Matchers with BeforeAndAfter with DBFixture {

  val companyId = 5L

  before {
    createSchema(transactor)
    insertCompany(id = companyId, name = "Despegar.com", staffCount = 2).unsafeRunSync()
  }

  "The Employee Store" should {
    "find all employees" in {
      (insertEmployee(id = 1, name = "Paul", age = Some(25), salary = 30000, startDate = LocalDate.now(), companyId = companyId) *>
        insertEmployee(id = 2, name = "Lisa", age = Some(25), salary = 35000, startDate = LocalDate.now(), companyId = companyId))
        .unsafeRunSync()

      val instance = new EmployeeStore
      val program: ConnectionIO[List[Employee]] = instance.findAll
      val employees = program.transact(transactor).unsafeRunSync()

      employees.map(e => e.name) should contain only ("Paul", "Lisa")
    }

    "retrieve employees using a filter" in {
      (insertEmployee(id = 6, name = "Louise", age = Some(30), salary = 35000, startDate = LocalDate.now().minusMonths(7), companyId = companyId) *>
        insertEmployee(id = 7, name = "Brian", age = Some(21), salary = 22000, startDate = LocalDate.now().minusMonths(3), companyId = companyId) *>
        insertEmployee(id = 8, name = "Samantha", age = Some(22), salary = 20000, startDate = LocalDate.now().minusDays(20), companyId = companyId) *>
        insertEmployee(id = 9, name = "Jill", age = Some(22), salary = 28000, startDate = LocalDate.now().minusMonths(2), companyId = companyId))
        .unsafeRunSync()
      val instance = new EmployeeStore
      val filter = EmployeeFilter(
        ids = List(),
        name = None,
        minimumAge = Some(22),
        startDateFrom = LocalDate.now().minusYears(5),
        startDateTo = LocalDate.now().minusMonths(1),
        offset = None
      )

      val employees = instance.findByFilter(filter).transact(transactor).unsafeRunSync()
      employees.map(_.id.get) should contain only (6L, 9L)
    }

    "notify a birthday =D" in {
      val employeeId = 6L
      insertEmployee(
        id = employeeId, name = "Louise", age = Some(30), salary = 35000,
        startDate = LocalDate.now().minusMonths(7), companyId = companyId)
        .unsafeRunSync()
      val instance = new EmployeeStore
      val program: ConnectionIO[Int] = instance.happyBirthday(employeeId)
      val updatedRows = program.transact(transactor).unsafeRunSync()

      updatedRows shouldBe 1
    }
  }
}
