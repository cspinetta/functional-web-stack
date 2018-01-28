package com.despegar.demo.model

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}


// TODO - Ver!
// import io.circe.generic.JsonCodec, io.circe.syntax._
// @JsonCodec case class Company(id: Option[Long], name: String, employees: List[Employee] = List())

case class Company(id: Option[Long], name: String, employees: List[Employee] = List())

object Company {
  import Employee._
  implicit val encoder: Encoder[Company] = deriveEncoder
  implicit val decoder: Decoder[Company] = deriveDecoder

}
