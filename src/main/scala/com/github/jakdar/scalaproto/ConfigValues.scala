package com.github.jakdar.scalaproto

import com.typesafe.config.Config
import com.github.jakdar.scalaproto.ConfigValues._
case class ConfigValues(
    app: ApplicationConfig,
    raw: Config
)

object ConfigValues {

  case class ApplicationConfig(
      bindHost: String,
      bindPort: Int,
      appLogLevel: String,
      rootLogLevel: String
  )
}
