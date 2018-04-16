package com.despegar.demo.client

import cats.effect.IO
import com.despegar.demo.model.{Company, Employee}
import io.circe.syntax._
import org.http4s.Uri
import org.http4s.client._
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.dsl.io._


/**
  *
  * == Company Client ==
  *
  * Usage example:
  *
  * {{{
  * import org.http4s.client.blaze._
  * import cats.effect.IO
  * import org.http4s.client._
  *
  * val httpClient: Client[IO] = Http1Client[IO](
  *     BlazeClientConfig.defaultConfig.copy(
  *       maxTotalConnections = 10,
  *       idleTimeout = 5 minutes,
  *       requestTimeout = 30 seconds))
  *     .unsafeRunSync()
  *
  * val companyClient = CompanyClient(httpClient)
  *
  * companyClient.getById(companyId = 10).unsafeRunSync() // result in Either[Throwable, Option[Company]]
  * }}}
  *
  * @param httpClient: Client manager
  */
case class CompanyClient(httpClient: Client[IO]) extends Http4sClientDsl[IO] {
  import com.despegar.demo.utils.CirceUtils.circeCustomSyntax._

  def getById(companyId: Long): IO[Either[Throwable, Option[Company]]] = {
    httpClient.expect(s"http://localhost:9290/demo/company/$companyId")(jsonOf[IO, Option[Company]]).attempt
  }

  def hire(companyId: Long, employee: Employee): IO[Either[Throwable, Unit]] =
    Uri.fromString(s"http://localhost:9290/demo/company/hire/$companyId").toOption match {
      case Some(uri) =>
        import Employee._
        httpClient.expect[Unit](POST(uri, employee.asJson)).attempt
      case None => IO.pure(Left(new RuntimeException(s"Invalid uri")))
    }

}