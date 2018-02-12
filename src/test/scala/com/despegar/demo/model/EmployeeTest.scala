package com.despegar.demo.model

import java.time.LocalDate

import com.despegar.demo.utils.LogSupport
import org.scalatest.{FunSuite, Matchers}
import io.circe.syntax._

class EmployeeTest extends FunSuite with Matchers with LogSupport {

  test("Serialization") {

    val employee = Employee(id = Some(1L), name = "theName", age = None, salary = BigDecimal.valueOf(23455), startDate = LocalDate.now())
    val serialized = employee.asJson
    log.debug(serialized.toString)
    val deserialized = serialized.as[Employee]

    deserialized match {
      case Right(e) => e shouldBe employee
      case Left(cause) => log.debug(cause.toString)
    }
  }

}
