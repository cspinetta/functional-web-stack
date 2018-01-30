package com.despegar.demo.store

import com.despegar.demo.db.DemoDS
import com.despegar.demo.model.Company
import doobie.imports._
import org.scalatest.{FunSuite, Matchers}
import fs2.interop.cats._

class CompanyStoreTest extends FunSuite with Matchers {

  test("findCompanyWithStaff") {
    val companyId = 6L

    val instance = new CompanyStore
    val program: ConnectionIO[Option[Company]] = instance.findCompanyWithStaff(companyId)
    val result: Option[Company] = program.transact(DemoDS.DemoTransactor).unsafeRun()

    result.isDefined shouldBe true
    result.get.name shouldBe "Despegar.com"
    result.get.employees.size shouldBe 2
    result.get.employees.map(_.name).contains("Pepe") shouldBe true
    result.get.employees.map(_.name).contains("Maria") shouldBe true
  }

  test("createCompanyTable") {
    val instance = new CompanyStore
    val program: ConnectionIO[Int] = instance.createCompanyTable
    program.transact(DemoDS.DemoTransactor).unsafeRun()
  }

  test("save") {
    val instance = new CompanyStore
    val program: ConnectionIO[Long] = instance.save("Hoteles.com")
    val newCompanyId: Long = program.transact(DemoDS.DemoTransactor).unsafeRun()
    System.out.println(s"New company id: $newCompanyId")
  }

  test("saveAll") {
    val instance = new CompanyStore
    val newCompanies = List("Almundo", "Booking")
    val program: ConnectionIO[Int] = instance.saveAll(newCompanies)
    val insertedRows: Long = program.transact(DemoDS.DemoTransactor).unsafeRun()
    insertedRows shouldBe newCompanies.size
  }

  test("insert") {
    val instance = new CompanyStore
    val program: ConnectionIO[Int] = instance.insert(id = 500L, name = "Tomas")
    val insertedRows: Long = program.transact(DemoDS.DemoTransactor).unsafeRun()
    insertedRows shouldBe 1
  }

  test("updateName") {
    val instance = new CompanyStore
    val program: ConnectionIO[Int] = instance.updateName(id = 500L, name = "testCompany")
    val insertedRows: Long = program.transact(DemoDS.DemoTransactor).unsafeRun()
    insertedRows shouldBe 1
  }

  test("safeInsert") {
    val instance = new CompanyStore
    val program: ConnectionIO[Int] = instance.safeInsert(id = 500L, name = "TestCompany")
    val insertedRows: Long = program.transact(DemoDS.DemoTransactor).unsafeRun()
    insertedRows shouldBe 1
  }

}
