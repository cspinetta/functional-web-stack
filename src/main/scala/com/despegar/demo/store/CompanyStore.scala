package com.despegar.demo.store

import java.sql.{SQLIntegrityConstraintViolationException, Timestamp}
import java.time.{LocalDate, LocalDateTime}

import com.despegar.demo.model._
import doobie.imports._

class CompanyStore() {

  implicit val DateTimeMeta: Meta[LocalDateTime] = Meta[Timestamp].xmap(ts => ts.toLocalDateTime, dt => Timestamp.valueOf(dt))


  def findCompanyWithStaff(companyId: Long): ConnectionIO[Option[Company]] = {
    val results = sql"""
         select c.id, c.name,
             		e.id, e.name, e.age, e.salary, e.start_date
         from Test.company c
         inner join Test.employee e on e.company_id = c.id
         where c.id = $companyId
      """.query[(Long, String,
      Long, String, Option[Int], BigDecimal, LocalDate)].map {
      case (companyId, companyName,
      employeeId, employeeName, employeeAge, employeeSalary, employeeStartDate) =>

        val employee = Employee(Some(employeeId), employeeName, employeeAge, employeeSalary, employeeStartDate)
        val company = Company(id = Some(companyId), name = companyName)
        (company, employee)

    }.list

    results.map(r => {
      val allEmployees: List[Employee] = r.map(_._2)
      r.headOption.map(tuple => tuple._1.copy(employees = allEmployees))
    })

    /*    results.flatMap(r => {
        val grouped: Map[Company, (Company, Employee)] = r.groupBy(_._1)
        val allEmployees = grouped.values.map(_._2).toList
        grouped.map(entry => entry._1.copy(employees = allEmployees)).headOption
    })
    */
  }


  def createCompanyTable: ConnectionIO[Int] =
    fr"""create table Test.company(
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


  // TODO - Ejeuctar alter table Test.company add column staff_count int(3) not null default 0;
  def incrementStaff(companyId: Long): ConnectionIO[Int] =
    fr"""update Test.company set staff_count = staff_count + 1 where id = $companyId)""".update.withUniqueGeneratedKeys[Int]("staff_count")



  def insert(id: Long, name: String): ConnectionIO[Int] =
    fr"""insert into Test.company(id, name) values ($id, $name)""".update.run

  def updateName(id: Long, name: String): ConnectionIO[Int] =
    fr"""update Test.company set name = $name where id = $id""".update.run

  def safeInsert(id: Long, name: String): ConnectionIO[Int] =
    insert(id, name).exceptSome {
      case e: SQLIntegrityConstraintViolationException => updateName(id, name) // another ConnectionIO[Int]
    }
}
