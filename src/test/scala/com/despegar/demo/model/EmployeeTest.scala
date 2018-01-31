package com.despegar.demo.model

import java.time.LocalDate

import org.scalatest.{FunSuite, Matchers}
import io.circe.syntax._

class EmployeeTest extends FunSuite with Matchers {

  test("Serialization") {

    val employee = Employee(id = Some(1L), name = "theName", age = None, salary = BigDecimal.valueOf(23455), startDate = LocalDate.now())
    val serialized = employee.asJson
    System.out.println(serialized)
    val deserialized = serialized.as[Employee]

    deserialized match {
      case Right(e) => e shouldBe employee
      case Left(cause) => System.out.print(cause)
    }
  }

}
