package com.despegar.demo.api

import java.time.LocalDate

case class EmployeeFilter(name: Option[String],
                          minimumAge: Option[Int],
                          startDateFrom: LocalDate,
                          startDateTo: LocalDate,
                          offset: Option[Long],
                          ids: List[Long] = List())
