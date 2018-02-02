package com.despegar.demo.store

import doobie.util.transactor.Transactor
import doobie.imports._
import cats.effect._

class TxEmployeeStore(transactor: Transactor[IO]) {

  def findAllNames: List[String] =
      sql"select name from Test.employee"   // Fragment
        .query[String]                      // Query0[String]
        .list                               // ConnectionIO[List[String]]
        .transact(transactor)               // Task[List[String]]
        .unsafeRun()                        // List[String]

}
