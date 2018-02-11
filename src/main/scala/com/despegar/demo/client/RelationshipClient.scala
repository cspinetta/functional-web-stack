package com.despegar.demo.client

import cats.effect.IO
import com.despegar.demo.model.Relationship
import org.http4s.client._
import org.http4s.client.blaze._

import scala.concurrent.duration._


object RelationshipClient extends RelationshipClient {

  val clientConfig = BlazeClientConfig.defaultConfig.copy(
    maxTotalConnections = 10,
    idleTimeout = 5.minutes,
    requestTimeout = 30.seconds
  )

  override val httpClient: Client[IO] = Http1Client[IO](clientConfig).unsafeRunSync()

}

trait RelationshipClient {

  import com.despegar.demo.utils.CirceUtils.circeCustomSyntax._

  def httpClient: Client[IO]

  def getNDegreeRelationshipsById(id: Long, degree: Int): IO[Either[Throwable, List[Relationship]]] = {
    httpClient.expect(s"http://localhost:9290/demo/relationships/$id")(jsonOf[IO, List[Relationship]]).attempt
  }

}