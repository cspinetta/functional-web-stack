package com.despegar.demo.store

import cats.data.NonEmptyList
import com.despegar.demo.api.EmployeeFilter
import com.despegar.demo.model._
import doobie._
import doobie.implicits._
import doobie.util.fragments.{whereAnd, whereAndOpt}

class EmployeeStore() {
  val pageSize = 100

  def findAll: ConnectionIO[List[Employee]] =
    sql"select id, name, age, salary, start_date from Test.employee"
      .query[Employee]
      .to[List]

  def findByFilter(filter: EmployeeFilter): ConnectionIO[List[Employee]] = {
    val idsCondition = NonEmptyList.fromList(filter.ids).map(ids => Fragments.in(fr"id", ids))
    val nameCondition: Option[Fragment] = filter.name.map(n => fr"name like $n")
    val ageCondition: Option[Fragment] = filter.minimumAge.map(age => fr"age >= $age")
    val fromCondition: Option[Fragment] = Some(fr"start_date >= ${filter.startDateFrom}")
    val toCondition: Option[Fragment] = Some(fr"start_date < ${filter.startDateTo}")

    val allConditions = Seq(idsCondition, nameCondition, ageCondition, fromCondition, toCondition)

    val q = fr"select id, name, age, salary, start_date from Test.employee" ++
          whereAndOpt(allConditions.toArray:_*) ++
          fr"limit $pageSize" ++
          filter.offset.map(off => fr"offset $off").getOrElse(Fragment.empty)

    q.query[Employee].to[List]
  }

  def happyBirthday(id: Long): ConnectionIO[Int] =
    fr"""update Test.employee set age = age + 1 where id = $id""".update.run

  def save(employee: Employee, companyId: Long): ConnectionIO[Long] =
    fr"""insert into Test.employee(name, age, salary, start_date, company_id)
         values (${employee.name},
                 ${employee.age},
                 ${employee.salary},
                 ${employee.startDate},
                 $companyId)"""
      .update.withUniqueGeneratedKeys[Long]("id")

  def findById(id: Long): ConnectionIO[Option[Employee]] = {
    val q = sql"select id, name, age, salary, start_date from Test.employee where id = $id"
    q.query[Employee].option
  }

  def findByIds(ids: NonEmptyList[Long]): ConnectionIO[List[Employee]] = {
    val q = fr"select id, name, age, salary, start_date from Test.employee" ++
      whereAnd(Fragments.in(fr"id", ids))
    q.query[Employee].to[List]
  }

}
