package com.despegar.demo.model

import java.time.LocalDate

import io.circe.{Decoder, Encoder}

case class Employee(id: Option[Long], name: String, age: Option[Int], salary: BigDecimal, startDate: LocalDate)


object Employee {
  implicit val encoder: Encoder[Employee] = Encoder.forProduct4("id", "name", "age", "salary")(u => (u.id, u.name, u.age, u.salary))
  implicit val decoder: Decoder[Employee] = Decoder.forProduct4[Option[Long], String, Option[Int], BigDecimal, Employee]("id", "name", "age", "salary") {
    case (id, name, age, salary) => Employee(id, name, age, salary, LocalDate.now())
  }
}


/*
// Slide: Circe unsupported types
object Employee {
  import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
  import com.despegar.demo.utils.LocalDateTimeCodec._

  implicit val encoder: Encoder[Employee] = deriveEncoder
  implicit val decoder: Decoder[Employee] = deriveDecoder
}
*/
