package com.despegar.demo.client

import cats.effect.IO
import com.despegar.demo.model.Relationship
import org.http4s.client._


case class RelationshipClient(httpClient: Client[IO]) {

  import com.despegar.demo.utils.CirceUtils.circeCustomSyntax._

  def getNDegreeRelationshipsById(id: Long, degree: Int): IO[Either[Throwable, List[Relationship]]] = {
    httpClient.expect(s"http://localhost:9290/demo/relationships/$id/degree/$degree")(jsonOf[IO, List[Relationship]]).attempt
  }

}