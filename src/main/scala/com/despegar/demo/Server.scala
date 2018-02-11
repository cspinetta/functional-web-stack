package com.despegar.demo

import java.util.concurrent.{ExecutorService, Executors}

import com.despegar.demo.db.DemoDS
import com.despegar.demo.program._
import com.despegar.demo.store._
import com.despegar.demo.utils.ThreadUtils._
import com.despegar.demo.api.{CompanyService, EmployeeService, HealthService}
import fs2.{Stream, StreamApp}
import fs2.StreamApp.ExitCode
import cats.effect._
import com.despegar.demo.client.RelationshipClient
import com.zaxxer.hikari.HikariDataSource
import doobie.util.transactor.Transactor.Aux
import org.http4s._
import org.http4s.server.blaze._
import org.http4s.server.Router

import scala.concurrent.ExecutionContext

object Server extends StreamApp[IO] with Stores with Programs {

  private val executor : ExecutorService  = Executors.newFixedThreadPool(30, namedThreadFactory("demo-server-pool"))
  implicit val ec: ExecutionContext = ExecutionContext.fromExecutor(executor)

  override def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, ExitCode] = {

    val router: HttpService[IO] = Router[IO](
      "/demo/employee" -> EmployeeService(transactor).service(employeeProgram, relationshipProgram),
      "/demo/company" -> CompanyService(transactor).service(companyProgram),
      "/" -> HealthService().service()
    )

    BlazeBuilder[IO]
      .bindHttp(9290, "localhost")
      .mountService(router)
      .serve
  }
}

trait Stores {
  val transactor: Aux[IO, HikariDataSource] = DemoDS.DemoTransactor

  val txCompanyStore = new TxEmployeeStore(transactor)
  val employeeStore = new EmployeeStore
  val companyStore = new CompanyStore

}
trait Clients {
  val relationshipClient: RelationshipClient.type = RelationshipClient
}

trait Programs extends Stores with Clients {

  val companyProgram = new CompanyProgram(companyStore, employeeStore)
  val employeeProgram = new EmployeeProgram(employeeStore)
  val relationshipProgram = new RelationshipProgram(employeeStore, relationshipClient)

}