package com.despegar.demo.client

import cats.effect.IO
import com.despegar.demo.model.{Company, Employee}
import org.http4s.Uri
import org.http4s.dsl.io._
import io.circe.syntax._
import org.http4s.client._
import org.http4s.client.blaze._
import org.http4s.client.dsl.Http4sClientDsl

import scala.concurrent.duration._

object CompanyClient extends CompanyClient {

  val clientConfig = BlazeClientConfig.defaultConfig.copy(
    maxTotalConnections = 10,
    idleTimeout = 5.minutes,
    requestTimeout = 30.seconds
  )

  override val httpClient: Client[IO] = Http1Client[IO](clientConfig).unsafeRunSync()

}

trait CompanyClient extends Http4sClientDsl[IO] {

  import com.despegar.demo.utils.CirceUtils.circeCustomSyntax._

  def httpClient: Client[IO]

  def getById(companyId: Long): Either[Throwable, Option[Company]] = {
    httpClient.expect(s"http://localhost:9290/demo/company/$companyId")(jsonOf[IO, Option[Company]]).attempt.unsafeRunSync()
  }

  def hire(companyId: Long, employee: Employee): Either[Throwable, Unit] =
    Uri.fromString(s"http://localhost:9290/demo/company/hire/$companyId").toOption match {
      case Some(uri) =>
        import Employee._
        httpClient.expect[Unit](POST(uri, employee.asJson)).attempt.unsafeRunSync()
      case None => Left(new RuntimeException(s"Invalid uri"))
    }

}