package com.despegar.demo.api

import com.despegar.demo.model.Employee
import com.despegar.demo.program.CompanyProgram
import com.despegar.demo.utils.LogSupport
import doobie.imports._
import fs2.Task
import fs2.interop.cats._
import org.http4s.{HttpService, Request}
import org.http4s.dsl._

class CompanyService (xa: Transactor[Task]) extends LogSupport {

  import com.despegar.demo.utils.CirceUtils.circeCustomSyntax._

  def service(companyProgram: CompanyProgram): HttpService = {

    def route = HttpService {
      case GET -> Root / LongVar(id) => handleGetById(id)
      case req@POST -> Root / "hire" / LongVar(companyId) => handleHire(req, companyId)
    }

    def handleHire(request: Request, companyId: Long) =
      request.decodeWith(jsonOf[Employee], strict = true) { newEmployee =>
        companyProgram.hire(companyId, newEmployee).transact(xa).attempt.flatMap {
          case Right(employeeList) => Ok()
          case Left(cause) =>
            log.error(s"Error saving new employee for company $companyId", cause)
            InternalServerError("Error saving new employee")
        }
      }

    def handleGetById(companyId: Long) =
      companyProgram.findCompanyWithStaff(companyId).transact(xa).attempt.flatMap {
        case Right(company) => Ok()
        case Left(cause) =>
          log.error(s"Error getting company $companyId", cause)
          InternalServerError("Error getting company")
      }

    route
  }

}

object CompanyService extends LogSupport {

  def apply(xa: Transactor[Task]): CompanyService = new CompanyService(xa)

}
