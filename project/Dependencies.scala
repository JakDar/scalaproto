object Dependencies {
  import sbt._
  val ScalaTestVersion = "3.2.10"
  val CatsParseVersion = "0.3.4"
  val CatsVersion      = "2.6.1"
  val MouseVersion     = "1.0.7"
  val GuavaVersion     = "30.1.1-jre"
  val DiffxVersion     = "0.6.0"
  val ScalametaVersion = "4.4.30"

  val catsDependencies = Seq(
    "org.typelevel" %% "cats-core" % CatsVersion,
    "org.typelevel" %% "mouse"     % MouseVersion,
  )

  private val miscDependencies = Seq(
    "org.typelevel"   %% "cats-parse" % CatsParseVersion,
    "com.google.guava" % "guava"      % GuavaVersion,
    "org.scalameta"   %% "scalameta"  % ScalametaVersion cross CrossVersion.for3Use2_13,
    "com.lihaoyi"     %% "ujson"      % "1.4.2",
    "com.lihaoyi"     %% "pprint"     % "0.6.6",
  )

  private val testDependencies = Seq(
    "org.scalatest"          %% "scalatest"       % ScalaTestVersion % Test,
    "com.softwaremill.diffx" %% "diffx-scalatest" % DiffxVersion     % Test cross CrossVersion.for3Use2_13
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
