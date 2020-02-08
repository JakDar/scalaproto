package com.github.jakdar.scalaproto

import com.typesafe.scalalogging.StrictLogging
import cats.effect.{ContextShift, IO}

import _root_.scala.concurrent.ExecutionContext

class Application(
    implicit ec: ExecutionContext,
    cs: ContextShift[IO]
) extends StrictLogging {

  // FIXME
  // LoggingConfigurator.setRootLogLevel(config.app.rootLogLevel)
  // LoggingConfigurator.setLogLevel("com.github.jakdar.scalaproto", config.app.appLogLevel)

}
