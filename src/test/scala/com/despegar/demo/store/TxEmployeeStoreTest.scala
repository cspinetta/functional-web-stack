package com.despegar.demo.store

import org.scalatest.{BeforeAndAfter, FunSuite}

class TxEmployeeStoreTest extends FunSuite with BeforeAndAfter with DBFixture {

  before {
    createSchema()
  }

  test("findAllNames ok") {
    val instance = new TxEmployeeStore(transactor)
    instance.findAllNames.foreach(println)
  }

}
