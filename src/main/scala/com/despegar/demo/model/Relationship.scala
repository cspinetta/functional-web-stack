package com.despegar.demo.model

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}


case class Relationship(from: Long, to: Long, degree: Int, by: List[Long])

object Relationship {

  implicit val encoder: Encoder[Relationship] = deriveEncoder
  implicit val decoder: Decoder[Relationship] = deriveDecoder
}
