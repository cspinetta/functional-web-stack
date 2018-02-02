package com.despegar.demo.api

import java.time.LocalDate

import com.despegar.demo.program.EmployeeProgram
import com.despegar.demo.utils.LogSupport
import cats.effect._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.implicits._
import doobie.imports._
import io.circe.syntax._

import scala.util.Try


class EmployeeService(xa: Transactor[IO]) extends LogSupport {

  import com.despegar.demo.utils.CirceUtils.circeCustomSyntax._
  import com.despegar.demo.model.Employee._
  import com.despegar.demo.api.EmployeeService._


  def service(employeeProgram: EmployeeProgram): HttpService[IO] = {

    def route = HttpService[IO] {
      case GET -> Root / "list" => handleGetAll
      case GET -> Root / LongVar(id) => handleFindById(id)
      case GET -> Root / "filter" / LocalDateVar(startDateFrom) / LocalDateVar(startDateTo):?
        NameQueryParamMatcher(name) +&
        MinimumAgeQueryParamMatcher(minimumAge) +&
        OffsetMatcher(offset) =>

        val filter = EmployeeFilter(name, minimumAge, startDateFrom, startDateTo, offset)
        handleFindByFilter(filter)

    }

    def handleGetAll: IO[Response[IO]] =
      employeeProgram.findAll.transact(xa).attempt.flatMap {
        case Right(employeeList) => Ok(employeeList.asJson)
        case Left(cause) =>
          log.error("Error getting all employees", cause)
          InternalServerError("Error getting all employees")
      }

    def handleFindById(id: Long): IO[Response[IO]] =
      employeeProgram.findById(id).transact(xa).attempt.flatMap {
        case Right(employee) => Ok(employee.asJson)
        case Left(cause) =>
          log.error(s"Error getting employee by id $id", cause)
          InternalServerError(s"Error getting employee by id $id")
      }


    def handleFindByFilter(filter: EmployeeFilter): IO[Response[IO]] =
      employeeProgram.findByFilter(filter).transact(xa).attempt.flatMap {
        case Right(employeeList) => Ok(employeeList.asJson)
        case Left(cause) =>
          log.error("Error finding employees by filter", cause)
          InternalServerError("Error finding employees by filter")
      }

    route

  }

}

object EmployeeService extends LogSupport {
  def apply(xa: Transactor[IO]): EmployeeService = new EmployeeService(xa)

  object NameQueryParamMatcher extends OptionalQueryParamDecoderMatcher[String]("name")

  object MinimumAgeQueryParamMatcher extends OptionalQueryParamDecoderMatcher[Int]("minimum-age")

  object OffsetMatcher extends OptionalQueryParamDecoderMatcher[Long]("offset")

  object LocalDateVar {
    def unapply(str: String): Option[LocalDate] = {
      if (!str.isEmpty)
        Try(LocalDate.parse(str)).toOption
      else
        None
    }
  }
}