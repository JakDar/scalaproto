object Dependencies {
  import sbt._
  val ScalaTestVersion = "3.2.7"
  val FastParseVersion = "2.3.2"
  val CatsVersion      = "2.5.0"
  val MouseVersion     = "1.0.2"
  val GuavaVersion     = "30.1.1-jre"
  val DiffxVersion     = "0.4.5"

  val catsDependencies = Seq(
    "org.typelevel" %% "cats-core" % CatsVersion,
    "org.typelevel" %% "mouse"     % MouseVersion,
    // "org.scalameta"        %% "scalameta"       % "4.4.11",
  )

  private val miscDependencies = Seq(
    "com.lihaoyi"     %% "fastparse" % FastParseVersion,
    "com.google.guava" % "guava"     % GuavaVersion,
  )

  private val testDependencies = Seq(
    "org.scalatest"          %% "scalatest"       % ScalaTestVersion % Test,
    "com.softwaremill.diffx" %% "diffx-scalatest" % DiffxVersion     % Test,
  )

  val all: Seq[ModuleID] = Seq(
    testDependencies,
    miscDependencies,
    catsDependencies,
  ).flatten

  val additionalResolvers: Seq[Resolver] = Seq(
    Resolver.mavenCentral,
    "Typesafe Repo" at "https://repo.typesafe.com/typesafe/releases/",
  )

}
