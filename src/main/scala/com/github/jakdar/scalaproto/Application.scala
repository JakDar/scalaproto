package com.github.jakdar.scalaproto

import cats.effect.Timer
import com.typesafe.scalalogging.StrictLogging
import cats.effect.{ContextShift, IO}

import scala.concurrent.ExecutionContext

class Application(config: ConfigValues)(
    implicit ec: ExecutionContext,
    cs: ContextShift[IO]
) extends StrictLogging {

  // FIXME
  // LoggingConfigurator.setRootLogLevel(config.app.rootLogLevel)
  // LoggingConfigurator.setLogLevel("com.github.jakdar.scalaproto", config.app.appLogLevel)

}
