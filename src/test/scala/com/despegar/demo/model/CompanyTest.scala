package com.despegar.demo.model

import java.time.LocalDate

import com.despegar.demo.utils.LogSupport
import io.circe.syntax._
import org.scalatest.{FunSuite, Matchers}

class CompanyTest extends FunSuite with Matchers with LogSupport {

  test("semiauto encoder & decoder") {
    val company = new Company(Some(1L), "testCompany")
    val serialized = company.asJson
    log.debug(serialized.toString)
    val deserialized = serialized.as[Company]

    Right(company) shouldBe deserialized
  }

  test("semiauto encoder & decoder - Company with employees") {
    val employee = new Employee(Some(22L), "theName", age = None, BigDecimal.valueOf(10000), LocalDate.now())
    val company = new Company(Some(1L), "testCompany", List(employee))
    val serialized = company.asJson
    log.debug(serialized.toString)
    val deserialized = serialized.as[Company]

    deserialized match {
      case Right(c) => c shouldBe company
      case Left(cause) => log.debug(serialized.toString)
    }
  }

}
