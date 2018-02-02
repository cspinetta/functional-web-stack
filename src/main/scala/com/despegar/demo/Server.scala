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
import org.http4s._, org.http4s.dsl.io._, org.http4s.implicits._
import org.http4s.server.blaze._
import org.http4s.server.Router
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.ExecutionContext

object Server extends StreamApp[IO] {
  import Stores._
  import Programs._

  private val executor : ExecutorService  = Executors.newFixedThreadPool(30, namedThreadFactory("demo-server-pool"))

  override def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, ExitCode] = {

    val router: HttpService[IO] = Router[IO](
      "/demo/employee" -> EmployeeService(transactor).service(employeeProgram),
      "/demo/company" -> CompanyService(transactor).service(companyProgram),
      "/" -> HealthService().service()
    )

    BlazeBuilder[IO]
      .bindHttp(9290, "localhost")
      .mountService(router)
      .withExecutionContext(ExecutionContext.fromExecutor(executor))
      .serve
  }
}

object Stores {
  val transactor = DemoDS.DemoTransactor

  val txCompanyStore = new TxEmployeeStore(transactor)
  val employeeStore = new EmployeeStore
  val companyStore = new CompanyStore

}

object Programs {
  import Stores._

  val companyProgram = new CompanyProgram(companyStore, employeeStore)
  val employeeProgram = new EmployeeProgram(employeeStore)

}