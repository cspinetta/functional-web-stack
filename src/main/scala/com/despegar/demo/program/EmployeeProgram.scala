package com.despegar.demo.program

import com.despegar.demo.api.EmployeeFilter
import com.despegar.demo.model.Employee
import com.despegar.demo.store.EmployeeStore
import doobie.hi.ConnectionIO

class EmployeeProgram(employeeStore: EmployeeStore) {

  def findById(id: Long): ConnectionIO[Option[Employee]] = employeeStore.findById(id)
  def findAll: ConnectionIO[List[Employee]] = employeeStore.findAll
  def findByFilter(filter: EmployeeFilter): ConnectionIO[List[Employee]] = employeeStore.findByFilter(filter)

}
