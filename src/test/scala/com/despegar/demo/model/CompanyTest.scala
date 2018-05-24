package com.despegar.demo.model

import java.time.LocalDate

import com.despegar.demo.utils.LogSupport
import io.circe.syntax._
import org.scalatest.{EitherValues, Matchers, WordSpecLike}

class CompanyTest extends WordSpecLike with Matchers with EitherValues with LogSupport {

  "The Company entity" should {
    "be serialized/deserialized correctly" in {
      val company = new Company(Some(1L), "testCompany")
      val serialized = company.asJson
      log.debug(serialized.toString)
      val deserialized = serialized.as[Company]

      deserialized.right.value shouldBe company
    }
    "be serialized/deserialized correctly when has employees" in {
      val employee = new Employee(Some(22L), "theName", age = None, BigDecimal.valueOf(10000), LocalDate.now())
      val company = new Company(Some(1L), "testCompany", List(employee))
      val serialized = company.asJson
      log.debug(serialized.toString)

      val deserialized = serialized.as[Company]

      deserialized.right.value shouldBe company
    }
  }
}
