package com.despegar.demo.program

import com.despegar.demo.model.{Company, Employee}
import com.despegar.demo.store.{CompanyStore, EmployeeStore}
import doobie.hi.ConnectionIO

class CompanyProgram(companyStore: CompanyStore, employeeStore: EmployeeStore) {

  def hire(companyId: Long, employee: Employee): ConnectionIO[Int] =
    for {
      _ <- employeeStore.save(employee, companyId)
      totalStaff <- companyStore.incrementStaff(companyId)
    } yield totalStaff

  def findCompanyWithStaff(id: Long): ConnectionIO[Option[Company]] = companyStore.findCompanyWithStaff(id)

}
