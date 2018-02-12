package com.despegar.demo.store

import java.time.LocalDate

import com.despegar.demo.model.Company
import cats.implicits._
import com.despegar.demo.utils.LogSupport
import doobie._
import doobie.implicits._
import org.scalatest.{BeforeAndAfter, FunSuite, Matchers}

class CompanyStoreTest extends FunSuite with Matchers with BeforeAndAfter with DBFixture with LogSupport {

  before {
    createSchema()
  }

  test("findCompanyWithStaff") {
    val companyId = 6L
    val name = "Despegar.com"

    (insertCompany(id = companyId, name = name, staffCount = 2) *>
      insertEmployee(id = 1, name = "Paul", age = Some(25), salary = 30000, startDate = LocalDate.now(), companyId = companyId) *>
      insertEmployee(id = 2, name = "Lisa", age = Some(25), salary = 35000, startDate = LocalDate.now(), companyId = companyId))
      .unsafeRunSync()

    val instance = new CompanyStore
    val program: ConnectionIO[Option[Company]] = instance.findCompanyWithStaff(companyId)
    val result: Option[Company] = program.transact(transactor).unsafeRunSync()

    result.isDefined shouldBe true
    result.get.name shouldBe name
    result.get.employees.size shouldBe 2
    result.get.employees.map(_.name).contains("Paul") shouldBe true
    result.get.employees.map(_.name).contains("Lisa") shouldBe true
  }

  test("createCompanyTable") {
    val instance = new CompanyStore
    val program: ConnectionIO[Int] = instance.createCompanyTable
    program.transact(transactor).unsafeRunSync()
  }

  test("save") {
    val instance = new CompanyStore
    val program: ConnectionIO[Long] = instance.save("Hoteles.com")
    val newCompanyId: Long = program.transact(transactor).unsafeRunSync()
    log.debug(s"New company id: $newCompanyId")
  }

  test("saveAll") {
    val instance = new CompanyStore
    val newCompanies = List("Almundo", "Booking")
    val program: ConnectionIO[Int] = instance.saveAll(newCompanies)
    val insertedRows = program.transact(transactor).unsafeRunSync()
    insertedRows shouldBe newCompanies.size
  }

  test("insert") {
    val instance = new CompanyStore
    val program: ConnectionIO[Int] = instance.insert(id = 500L, name = "Tomas")
    val insertedRows = program.transact(transactor).unsafeRunSync()
    insertedRows shouldBe 1
  }

  test("updateName") {
    insertCompany(id = 9990, name = "Old Name Inc", staffCount = 0).unsafeRunSync()
    val instance = new CompanyStore
    val program: ConnectionIO[Int] = instance.updateName(id = 9990L, name = "New Name Inc")
    val insertedRows = program.transact(transactor).unsafeRunSync()
    insertedRows shouldBe 1
  }

  test("safeInsert") {
    val instance = new CompanyStore
    val program: ConnectionIO[Int] = instance.safeInsert(id = 500L, name = "TestCompany")
    val insertedRows = program.transact(transactor).unsafeRunSync()
    insertedRows shouldBe 1
  }

}
