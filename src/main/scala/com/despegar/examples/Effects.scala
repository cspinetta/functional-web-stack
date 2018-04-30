package com.despegar.examples

object Effects {

  def divideSideEffect(dividend: Int, divisor: Int): Int = {
    if (divisor == 0)
      throw new RuntimeException("Divisor can't be 0")
    dividend / divisor
  }

  def divide(dividend: Int, divisor: Int): Either[String, Int] = {
    if (divisor == 0) Left("Divisor can't be 0")
    else              Right(dividend / divisor)
  }

}
