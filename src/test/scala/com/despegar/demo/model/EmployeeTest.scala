package com.despegar.demo.model

import java.time.LocalDate

import com.despegar.demo.utils.LogSupport
import io.circe.syntax._
import org.scalatest.{EitherValues, Matchers, WordSpecLike}

class EmployeeTest extends WordSpecLike with Matchers with EitherValues with LogSupport {

  "The Employee entity" should {
    "be serialized/deserialized correctly" in {
      val employee = Employee(id = Some(1L), name = "theName", age = None, salary = BigDecimal.valueOf(23455), startDate = LocalDate.now())
      val serialized = employee.asJson
      log.debug(serialized.toString)
      val deserialized = serialized.as[Employee]

      deserialized.right.value shouldBe employee
    }
  }
}
