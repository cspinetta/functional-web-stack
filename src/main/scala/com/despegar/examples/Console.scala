package com.despegar.examples

import scala.collection.mutable
import scala.io.StdIn
import scala.util.Try

/**
  * Data type to describe the console operations supported
  * @tparam A: Type value produced by this Console description
  */
sealed trait Console[A] { self =>

  def run: A

  def flatMap[B](f: A => Console[B]): Console[B] =
    new Console[B] { def run: B = f(self.run).run }

  def map[B](f: A => B): Console[B] =
    new Console[B] { def run: B = f(self.run) }

  // ...
}

/**
  * Console Operation provider
  */
abstract class ConsoleOps {
  def ReadLn: Console[Option[String]]
  def PrintLn(line: String): Console[Unit]
}

/**
  * A program that convert integer to its binary representation
  * @param ops: console operation provider
  */
class Program(ops: ConsoleOps) {

  def binaryConverter: Console[Unit] = {
    for {
      _    <- ops.PrintLn("Enter a positive integer:")
      line <- ops.ReadLn
      _    <- ops.PrintLn(response(line))
    } yield ()
  }

  /*
  - Desugar of the previous for-comprehension:

  def binaryConverter: Console[Unit] = {
    ops.PrintLn("Enter a positive integer:")
      .flatMap(_ => ops.ReadLn
        .flatMap(line => ops.PrintLn(response(line))
          .map(_ => () )))
  }
   */

  private def response(line: Option[String]): String =
    line
      .flatMap(rawInput => stringToInt(rawInput))
      .map(decimalToBinary)
      .map(value => s"Binary representation: $value")
      .getOrElse(s"Wrong value introduced: $line")

  private def stringToInt(value: String): Option[Int] =
    Try(value.toInt).toOption

  private def decimalToBinary(decimal: Int): String =
    Integer.toBinaryString(decimal)
}

/**
  * An interpreter of the Console Operations that produce actions on
  * the standard input/output.
  */
object TerminalInterpreter extends ConsoleOps {
  def ReadLn: Console[Option[String]] =
    new Console[Option[String]] { def run = Option(StdIn.readLine()) }

  def PrintLn(line: String): Console[Unit] =
    new Console[Unit] { def run: Unit = println(line) }
}

/**
  * An interpreter of the Console Operations with facilities for testing purposes.
  */
case class TestingInterpreter(readValue: Option[String]) extends ConsoleOps {
  private val acc = mutable.ListBuffer.empty[String]

  def ReadLn: Console[Option[String]] =
    new Console[Option[String]] { def run: Option[String] = readValue }

  def PrintLn(line: String): Console[Unit] =
    new Console[Unit] { def run: Unit = acc += line }

  def extractValues: List[String] = acc.toList
}

object Runner extends App {
  new Program(TerminalInterpreter)
    .binaryConverter
    .run
}
