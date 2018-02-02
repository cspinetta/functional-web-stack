package com.despegar.demo.model

import java.time.LocalDate

import io.circe.syntax._
import org.scalatest.{FunSuite, Matchers}

class CompanyTest extends FunSuite with Matchers {

  test("semiauto encoder & decoder") {
    val company = new Company(Some(1L), "testCompany")
    val serialized = company.asJson
    System.out.println(serialized)
    val deserialized = serialized.as[Company]

    Right(company) shouldBe deserialized
  }

  test("semiauto encoder & decoder - Company with employees") {
    val employee = new Employee(Some(22L), "theName", age = None, BigDecimal.valueOf(10000), LocalDate.now())
    val company = new Company(Some(1L), "testCompany", List(employee))
    val serialized = company.asJson
    System.out.println(serialized)
    val deserialized = serialized.as[Company]

    deserialized match {
      case Right(c) => c shouldBe company
      case Left(cause) => System.out.println(cause)
    }
  }

}
