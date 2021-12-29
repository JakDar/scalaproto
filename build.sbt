name := "scalaproto"

scalaVersion := "3.0.2"

version := "0.1"
lazy val `scalaproto` = (project in file("."))
  .settings(
    organization             := "com.github.jakdar",
    name                     := "com.github.jakdar.scalaproto",
    resolvers ++= Dependencies.additionalResolvers,
    libraryDependencies ++= Dependencies.all,
    scalacOptions ++= CompilerOps.all,
    Test / parallelExecution := true,
  )

assembly / test := {}
assembly / assemblyJarName := "scalaproto.jar"
