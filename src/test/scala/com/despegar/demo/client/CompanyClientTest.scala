package com.despegar.demo.client

import java.time.LocalDate

import com.despegar.demo.model.Employee
import org.scalatest.{FunSuite, Matchers}

class CompanyClientTest extends FunSuite with Matchers {

  test("Get company") {
    val companyId = 6L

    CompanyClient.getById(companyId) match {
      case Right(maybeCompany) =>
        maybeCompany match {
          case Some(company) => System.out.println(company)
          case None => System.out.print(s"Company $companyId not found")
      }
      case Left(cause) => System.out.println(cause)
    }
  }

  test("Hire employee") {
    val employee = Employee(id = None, name = "test", age = None, salary = BigDecimal.valueOf(123456), startDate = LocalDate.now())
    CompanyClient.hire(9L, employee) match {
      case Right(_) => System.out.println("Hired ok")
      case Left(cause) => System.out.println(cause)
    }

  }

}
