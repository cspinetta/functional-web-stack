package com.despegar.demo.store

import com.despegar.demo.db.DemoDS
import org.scalatest.FunSuite

class TxEmployeeStoreTest extends FunSuite {

  test("findAllNames ok") {
    val instance = new TxEmployeeStore(DemoDS.DemoTransactor)
    instance.findAllNames.foreach(println)
  }

}
