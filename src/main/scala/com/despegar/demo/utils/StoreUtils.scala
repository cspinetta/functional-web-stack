package com.despegar.demo.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object StoreUtils {

  def truncateDate(datetime: String): LocalDate = {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    LocalDate.parse(datetime.substring(0, 10),formatter)
  }
}
