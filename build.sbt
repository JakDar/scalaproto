name := "scalaproto"

scalaVersion := "2.13.6"
version := "0.0"
lazy val `scalaproto` = (project in file("."))
  .settings(
    organization := "com.github.jakdar",
    name := "com.github.jakdar.scalaproto",
    resolvers ++= Dependencies.additionalResolvers,
    libraryDependencies ++= Dependencies.all,
    scalacOptions ++= CompilerOps.all,
    Test / parallelExecution := false,
  )

assembly / test := {}
assembly / assemblyJarName := "scalaproto.jar"
