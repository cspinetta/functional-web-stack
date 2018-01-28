package com.despegar.demo.model

import java.time.LocalDate

import io.circe.{Decoder, Encoder}

// TODO - Ver!
// import io.circe.generic.extras._, io.circe.syntax._
// implicit val config: Configuration = Configuration.default.withSnakeCaseMemberNames
// @ConfiguredJsonCodec case class Employee(id: Long, name: String, age: Option[Int], salary: BigDecimal, startDate: LocalDate)

case class Employee(id: Option[Long], name: String, age: Option[Int], salary: BigDecimal, startDate: LocalDate)

object Employee {

  import com.despegar.demo.utils.LocalDateTimeCodec._
  implicit val encoder: Encoder[Employee] = Encoder.forProduct3("id", "name", "start_date")(u => (u.id, u.name, u.startDate))
  implicit val decoder: Decoder[Employee] = Decoder.forProduct4[Long, String, Option[Int], BigDecimal, Employee]("id", "name", "age", "salary") {
    case (id, name, age, salary) => Employee(Some(id), name, age, salary, LocalDate.now())
  }

  // TODO - Ver!
  // val employee = Employee(1L, "Ale", Some(34), BigDecimal.valueOf(10000), LocalDate.now())
  // val json = employee.asJson

}

