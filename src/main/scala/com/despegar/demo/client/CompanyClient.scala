package com.despegar.demo.client

import com.despegar.demo.model.{Company, Employee}
import org.http4s._
import org.http4s.dsl._
import org.http4s.client._
import org.http4s.client.blaze._
import scala.concurrent.duration._
import io.circe.syntax._


object CompanyClient extends CompanyClient {

  val clientConfig = BlazeClientConfig.defaultConfig.copy(
    idleTimeout = 5.minutes,
    requestTimeout = 30.seconds
  )
  override val httpClient: Client = PooledHttp1Client(maxTotalConnections = 10, clientConfig)

}

trait CompanyClient {

  import com.despegar.demo.utils.CirceUtils.circeCustomSyntax._

  def httpClient: Client

  def getById(companyId: Long): Either[Throwable, Option[Company]] = {
    httpClient.expect(s"http://localhost:9290/demo/company/$companyId")(jsonOf[Option[Company]]).unsafeAttemptRun()
  }

  def hire(companyId: Long, employee: Employee): Either[Throwable, Unit] =
    Uri.fromString(s"http://localhost:9290/demo/hire/$companyId").toOption match {
      case Some(uri) =>
        import Employee._
        httpClient.expect[Unit](POST(uri, employee.asJson)).unsafeAttemptRun()
      case None => Left(new RuntimeException(s"Invalid uri"))
    }

}