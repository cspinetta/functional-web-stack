package com.despegar.demo.store

import cats.effect.IO
import doobie.util.transactor.Transactor
import doobie.implicits._

class TxEmployeeStore(transactor: Transactor[IO]) {

  def findAllNames: List[String] =
      sql"select name from Test.employee"   // Fragment
        .query[String]                      // Query0[String]
        .to[List]                           // ConnectionIO[List[String]]
        .transact(transactor)               // IO[List[String]]
        .unsafeRunSync()                    // List[String]
}
