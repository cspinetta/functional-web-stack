package com.despegar.demo.model


import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

case class Company(companyId: Option[Long], name: String, employees: List[Employee] = List())

object Company {
  implicit val encoder: Encoder[Company] = deriveEncoder
  implicit val decoder: Decoder[Company] = deriveDecoder
}


/*
// Slide: JsonCodec
import io.circe.generic.JsonCodec
import io.circe.syntax._

@JsonCodec case class Company(companyId: Option[Long], name: String, employees: List[Employee] = List())
*/

/*
// Slide: extras
import io.circe.syntax._
import io.circe.generic.extras._

object Company {
  implicit val config: Configuration = Configuration.default.withSnakeCaseKeys
}

@ConfiguredJsonCodec case class Company(companyId: Option[Long], name: String, employees: List[Employee] = List())
*/