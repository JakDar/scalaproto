enablePlugins(SbtNativePackager)
enablePlugins(JavaAppPackaging)
//addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")

scalaVersion := "2.13.1"
version := "0.0"
lazy val `scalaproto` = (project in file("."))
  .settings(
    organization := "com.github.jakdar",
    name := "com.github.jakdar.scalaproto",
    scalaVersion := "2.13.1",
    resolvers ++= Dependencies.additionalResolvers,
    libraryDependencies ++= Dependencies.all,
    scalacOptions ++= CompilerOps.all,
    parallelExecution in Test := false
  )
