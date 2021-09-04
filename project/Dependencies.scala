object Dependencies {
  import sbt._
  val ScalaTestVersion = "3.2.9"
  val FastParseVersion = "2.3.2"
  val CatsVersion      = "2.6.1"
  val MouseVersion     = "1.0.4"
  val GuavaVersion     = "30.1.1-jre"
  val DiffxVersion     = "0.5.6"
  val ScalametaVersion = "4.4.27"

  val catsDependencies = Seq(
    "org.typelevel" %% "cats-core" % CatsVersion,
    "org.typelevel" %% "mouse"     % MouseVersion,
  )

  private val miscDependencies = Seq(
    "com.lihaoyi"     %% "fastparse" % FastParseVersion,
    "com.google.guava" % "guava"     % GuavaVersion,
    "org.scalameta"   %% "scalameta" % ScalametaVersion,
    "com.lihaoyi"     %% "ujson"     % "1.4.0",
    "com.lihaoyi"     %% "pprint"    % "0.6.6",
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
