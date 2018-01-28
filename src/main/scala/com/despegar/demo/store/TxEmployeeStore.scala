package com.despegar.demo.store

import doobie.util.transactor.Transactor
import doobie.imports._
import fs2.Task
import fs2.interop.cats._

class TxEmployeeStore(transactor: Transactor[Task]) {

  def findAllNames: List[String] =
      sql"select name from Test.employee"
        .query[String]          // Query0[String]
        .list                   // ConnectionIO[List[String]]
        .transact(transactor)   // Task[List[String]]
        .unsafeRun()            // List[String]

}
