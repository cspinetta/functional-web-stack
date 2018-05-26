package com.despegar.demo.program

import com.despegar.demo.model.{Company, Employee}
import com.despegar.demo.store.{CompanyStore, EmployeeStore}
import doobie.hi.ConnectionIO

class CompanyProgram(companyStore: CompanyStore, employeeStore: EmployeeStore) {

  def save(company: Company): ConnectionIO[Long] = companyStore.save(company.name)

  def hire(companyId: Long, employee: Employee): ConnectionIO[Long] =
    for {
      employeeId <- employeeStore.save(employee, companyId)
      _ <- companyStore.incrementStaff(companyId)
    } yield employeeId

  def findCompanyWithStaff(id: Long): ConnectionIO[Option[Company]] = companyStore.findCompanyWithStaff(id)

}
