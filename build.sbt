name := "scalaproto"

scalaVersion := "3.1.1"

version := "0.3"
lazy val `scalaproto` = (project in file("."))
  .settings(
    organization             := "com.github.jakdar",
    name                     := "com.github.jakdar.scalaproto",
    resolvers ++= Dependencies.additionalResolvers,
    libraryDependencies ++= Dependencies.all,
    scalacOptions += "-no-indent",
    scalacOptions -= "-Xfatal-warnings",
    Test / parallelExecution := true,
  )

assembly / test := {}
assembly / assemblyJarName := "scalaproto.jar"
