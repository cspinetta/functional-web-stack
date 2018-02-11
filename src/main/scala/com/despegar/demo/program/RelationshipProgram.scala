package com.despegar.demo.program

import cats.data.{EitherT, NonEmptyList}
import cats.effect.IO
import com.despegar.demo.client.RelationshipClient
import com.despegar.demo.model.{Employee, Relationship}
import com.despegar.demo.store.EmployeeStore
import doobie.Transactor
import doobie.implicits._

class RelationshipProgram(employeeStore: EmployeeStore, connectionsClient: RelationshipClient) {

  def search(id: Long, nDegree: Int)(implicit xa: Transactor[IO]): IO[Either[String, List[Employee]]] = {
    val valOrError: EitherT[IO, String, List[Employee]] = for {
      relationships <- EitherT(connectionsClient.getNDegreeRelationshipsById(id, nDegree)).leftMap(exc => s"Service error: $exc")
      employees <- EitherT.liftF(employees(id, relationships))
    } yield employees

    valOrError.value
  }

  private def employees(reference: Long, relationships: List[Relationship])(implicit xa: Transactor[IO]): IO[List[Employee]] = {
    val ids = NonEmptyList.of(reference, relationships.map(_.to): _*)
    employeeStore.findByIds(ids).transact(xa)
  }
}
