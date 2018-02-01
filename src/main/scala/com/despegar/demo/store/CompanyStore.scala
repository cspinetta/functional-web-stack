package com.despegar.demo.store

import java.sql.{SQLIntegrityConstraintViolationException, Timestamp}
import java.time.LocalDate

import com.despegar.demo.model._
import doobie.imports._

class CompanyStore() extends DemoStore {

  def findCompanyWithStaff(companyId: Long): ConnectionIO[Option[Company]] = {
    val results = sql"""
         select c.id, c.name,
             		e.id, e.name, e.age, e.salary, e.start_date
         from Test.company c
         left join Test.employee e on e.company_id = c.id
         where c.id = $companyId
      """.query[(Long, String,
                Long, String, Option[Int], BigDecimal, LocalDate)].map {
      case (companyId, companyName,
            employeeId, employeeName, employeeAge, employeeSalary, employeeStartDate) =>

        val employee = Employee(Some(employeeId), employeeName, employeeAge, employeeSalary, employeeStartDate)
        val company = Company(companyId = Some(companyId), name = companyName)
        (company, employee)
    }.list

    results.map(tuples => {
      val allEmployees: List[Employee] = tuples.map(_._2)
      tuples.headOption.map(tuple => tuple._1.copy(employees = allEmployees))
    })
  }


  def createCompanyTable: ConnectionIO[Int] =
    fr"""create table Test.company1(
            id int(11) NOT NULL AUTO_INCREMENT,
            name varchar(255) NOT NULL,
            primary key(id)
        )
      """.update.run


  def save(name: String): ConnectionIO[Long] =
    fr"""insert into Test.company(name) values ($name)""".update.withUniqueGeneratedKeys[Long]("id")


  def saveAll(names: List[String]): ConnectionIO[Int] = {
    import cats.implicits._

    val sql = "insert into Test.company(name) values (?)"
    Update[String](sql).updateMany(names)
  }


  def incrementStaff(companyId: Long): ConnectionIO[Int] = {
    fr"""update Test.company set staff_count = staff_count + 1 where id = $companyId""".update.run
  }



  def insert(id: Long, name: String): ConnectionIO[Int] =
    fr"""insert into Test.company(id, name) values ($id, $name)""".update.run

  def updateName(id: Long, name: String): ConnectionIO[Int] =
    fr"""update Test.company set name = $name where id = $id""".update.run

  def safeInsert(id: Long, name: String): ConnectionIO[Int] =
    insert(id, name).exceptSome {
      case e: SQLIntegrityConstraintViolationException => updateName(id, name) // Important! Another ConnectionIO[Int]
    }
}
