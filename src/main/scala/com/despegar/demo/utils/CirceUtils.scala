package com.despegar.demo.utils

import io.circe.Printer
import org.http4s.circe.CirceInstances

object CirceUtils {
  val circeCustomSyntax = CirceInstances.withPrinter(Printer.noSpaces.copy(dropNullValues = true))
}
