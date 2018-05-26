package com.despegar.demo.api

import cats.effect._
import com.despegar.demo.model.{Company, Employee}
import com.despegar.demo.program.CompanyProgram
import com.despegar.demo.utils.LogSupport
import doobie._
import doobie.implicits._
import io.circe.syntax._
import org.http4s._
import org.http4s.dsl.io._

class CompanyService (xa: Transactor[IO]) extends LogSupport {

  import com.despegar.demo.model.Company._
  import com.despegar.demo.model.Employee._
  import com.despegar.demo.utils.CirceUtils.circeCustomSyntax._


  def service(companyProgram: CompanyProgram): HttpService[IO] = {

    def route: HttpService[IO] = HttpService[IO] {
      case GET      -> Root / LongVar(id)                 => handleGetById(id)
      case req@POST -> Root                               => handlePost(req)
      case req@POST -> Root / "hire" / LongVar(companyId) => handleHire(req, companyId)
    }

    def handlePost(request: Request[IO]): IO[Response[IO]] =
      request.decodeWith(jsonOf[IO, Company], strict = true) { company =>
        companyProgram.save(company).transact(xa).attempt.flatMap {
          case Right(_) => Ok()
          case Left(cause) =>
            log.error(s"Error saving new company ${company.name}", cause)
            InternalServerError("Error saving new company")
        }
      }

    def handleHire(request: Request[IO], companyId: Long): IO[Response[IO]] =
      request.decodeWith(jsonOf[IO, Employee], strict = false) { newEmployee =>
        companyProgram.hire(companyId, newEmployee).transact(xa).attempt.flatMap {
          case Right(employeeId) => Ok()
          case Left(cause) =>
            log.error(s"Error saving new employee for company $companyId", cause)
            InternalServerError("Error saving new employee")
        }
      }

    def handleGetById(companyId: Long): IO[Response[IO]] =
      companyProgram.findCompanyWithStaff(companyId).transact(xa).attempt.flatMap {
        case Right(Some(company)) => Ok(company.asJson)
        case Right(None) => NotFound()
        case Left(cause) =>
          log.error(s"Error getting company $companyId", cause)
          InternalServerError("Error getting company")
      }

    route
  }

}

object CompanyService extends LogSupport {
  def apply(xa: Transactor[IO]): CompanyService = new CompanyService(xa)

}
