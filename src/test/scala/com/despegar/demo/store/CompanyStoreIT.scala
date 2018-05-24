package com.despegar.demo.store

import java.time.LocalDate

import cats.implicits._
import com.despegar.demo.model.Company
import com.despegar.demo.utils.LogSupport
import doobie._
import doobie.implicits._
import org.scalatest.{BeforeAndAfter, Matchers, OptionValues, WordSpecLike}

class CompanyStoreIT extends WordSpecLike with Matchers with BeforeAndAfter with OptionValues with DBFixture with LogSupport {

  before {
    createSchema()
  }

  "The Company Store" should {
    "retrieve a company by ID" in {
      val companyId = 6L
      val name = "Despegar.com"

      (insertCompany(id = companyId, name = name, staffCount = 2) *>
        insertEmployee(id = 1, name = "Paul", age = Some(25), salary = 30000, startDate = LocalDate.now(), companyId = companyId) *>
        insertEmployee(id = 2, name = "Lisa", age = Some(25), salary = 35000, startDate = LocalDate.now(), companyId = companyId))
        .unsafeRunSync()

      val instance = new CompanyStore
      val program: ConnectionIO[Option[Company]] = instance.findCompanyWithStaff(companyId)
      val result: Option[Company] = program.transact(transactor).unsafeRunSync()

      result.value.name shouldBe name
      result.value.employees.size shouldBe 2
      result.value.employees.map(_.name).contains("Paul") shouldBe true
      result.value.employees.map(_.name).contains("Lisa") shouldBe true
    }

    "create the company table" in {
      val instance = new CompanyStore
      val program: ConnectionIO[Int] = instance.createCompanyTable
      program.transact(transactor).unsafeRunSync()
    }

    "save a new Company" in {
      val instance = new CompanyStore
      val program: ConnectionIO[Long] = instance.save("Hoteles.com")
      val newCompanyId: Long = program.transact(transactor).unsafeRunSync()
      newCompanyId.toInt should be > 0
      log.debug(s"New company id: $newCompanyId")
    }

    "save multiple new Company" in {
      val instance = new CompanyStore
      val newCompanies = List("Almundo", "Booking")
      val program: ConnectionIO[Int] = instance.saveAll(newCompanies)
      val insertedRows = program.transact(transactor).unsafeRunSync()
      insertedRows shouldBe newCompanies.size
    }

    "insert" in {
      val instance = new CompanyStore
      val program: ConnectionIO[Int] = instance.insert(id = 500L, name = "Tomas")
      val insertedRows = program.transact(transactor).unsafeRunSync()
      insertedRows shouldBe 1
    }

    "update the name" in {
      insertCompany(id = 9990, name = "Old Name Inc", staffCount = 0).unsafeRunSync()
      val instance = new CompanyStore
      val program: ConnectionIO[Int] = instance.updateName(id = 9990L, name = "New Name Inc")
      val insertedRows = program.transact(transactor).unsafeRunSync()
      insertedRows shouldBe 1
    }

    "save insert" in {
      val instance = new CompanyStore
      val program: ConnectionIO[Int] = instance.safeInsert(id = 500L, name = "TestCompany")
      val insertedRows = program.transact(transactor).unsafeRunSync()
      insertedRows shouldBe 1
    }
  }
}
