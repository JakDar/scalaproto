package com.github.jakdar.scalaproto

import cats.effect.{ExitCode, IO, IOApp}
import com.typesafe.config.ConfigFactory
import scala.concurrent.ExecutionContext.Implicits.global

object Main extends IOApp {

  val app = new Application(???)

  override def run(args: List[String]): IO[ExitCode] = ???

}
