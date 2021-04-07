object Dependencies {
  import sbt._
  val ScalaTestVersion  = "3.2.3"
  val ScalaMockVersion  = "5.0.0"
  val FastParseVersion  = "2.3.0"
  val CatsVersion       = "2.3.0"
  val CatsEffectVersion = "2.3.0"
  val MouseVersion      = "0.26.2"
  val GuavaVersion      = "30.0-jre"
  val DiffxVersion      = "0.3.30"
  val ScalaPbVersion    = "0.11.1"

  val catsDependencies = Seq(
    "org.typelevel" %% "cats-core"   % CatsVersion,
    "org.typelevel" %% "cats-effect" % CatsEffectVersion,
    "org.typelevel" %% "mouse"       % MouseVersion,
    // "org.scalameta"        %% "scalameta"       % "4.4.11",
    "com.lihaoyi"   %% "pprint"      % "0.5.7",
  )

  private val miscDependencies = Seq(
    "com.lihaoyi"     %% "fastparse" % FastParseVersion,
    "com.google.guava" % "guava"     % GuavaVersion,
  )

  private val testDependencies = Seq(
    "org.scalatest"          %% "scalatest"       % ScalaTestVersion % Test,
    "org.scalamock"          %% "scalamock"       % ScalaMockVersion % Test,
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
