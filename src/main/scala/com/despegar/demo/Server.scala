package com.despegar.demo

import java.util.concurrent.{ExecutorService, Executors}

import cats.effect._
import com.despegar.demo.api.{CompanyService, EmployeeService, HealthService}
import com.despegar.demo.client.RelationshipClient
import com.despegar.demo.conf.ConfigSupport
import com.despegar.demo.db.{DataSourceProvider, DemoSchema}
import com.despegar.demo.program._
import com.despegar.demo.store._
import com.despegar.demo.utils.ThreadUtils._
import doobie.util.transactor.Transactor
import fs2.StreamApp.ExitCode
import fs2.{Stream, StreamApp}
import org.http4s._
import org.http4s.client.Client
import org.http4s.client.blaze.{BlazeClientConfig, Http1Client}
import org.http4s.server.Router
import org.http4s.server.blaze._

import scala.concurrent.ExecutionContext

object Server extends StreamApp[IO] with Stores with Programs with ClientFactory {

  private val executor : ExecutorService  = Executors.newFixedThreadPool(30, namedThreadFactory("demo-server-pool"))
  implicit val ec: ExecutionContext = ExecutionContext.fromExecutor(executor)

  override def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, ExitCode] = {

    def router(client: Client[IO]): HttpService[IO] = Router[IO](
      "/demo/employee" -> EmployeeService(transactor).service(employeeProgram, relationshipProgram(client)),
      "/demo/company" -> CompanyService(transactor).service(companyProgram),
      "/" -> HealthService().service()
    )

    for {
      client   <- httpClient
      exitCode <- BlazeBuilder[IO]
        .bindHttp(9290, "localhost")
        .mountService(router(client))
        .serve
    } yield exitCode
  }
}

trait Stores extends ConfigSupport {
  lazy val transactor: Transactor[IO] = {
    if (config.db.useDemo) {
      val t = DataSourceProvider.H2TransactorInstance
      DemoSchema.createSchema(t)
      t
    }
    else DataSourceProvider.DSTransactor
  }

  lazy val txCompanyStore = new TxEmployeeStore(transactor)
  lazy val employeeStore = new EmployeeStore
  lazy val companyStore = new CompanyStore

}
trait Clients {
  def relationshipClient(client: Client[IO]): RelationshipClient = RelationshipClient(client)
}

trait Programs extends Stores with Clients {

  lazy val companyProgram = new CompanyProgram(companyStore, employeeStore)
  lazy val employeeProgram = new EmployeeProgram(employeeStore)
  def relationshipProgram(client: Client[IO]) = new RelationshipProgram(employeeStore, relationshipClient(client))
}

trait ClientFactory extends ConfigSupport {

  private lazy val clientConfig = BlazeClientConfig.defaultConfig.copy(
    maxTotalConnections = config.client.maxTotalConnections,
    idleTimeout = config.client.idleTimeout,
    requestTimeout = config.client.requestTimeout
  )

  def httpClient: Stream[IO, Client[IO]] = Http1Client.stream[IO](clientConfig)
}