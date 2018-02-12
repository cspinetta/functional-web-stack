package com.despegar.demo.client

import cats.effect.IO
import com.despegar.demo.model.Relationship
import org.scalatest.{FunSuite, Matchers}

class RelationshipClientTest extends FunSuite with Matchers {

  // TODO
  test("Get relationships") {
    val employeeId = 5L
    val degree = 2
    val getEncoded: IO[Either[Throwable, List[Relationship]]] = RelationshipClient.getNDegreeRelationshipsById(employeeId, 2)
  }

}
