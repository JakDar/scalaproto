object Dependencies {
  import sbt._
  val ScalaTestVersion  = "3.2.3"
  val ScalaMockVersion  = "5.0.0"
  val SimulacrumVersion = "0.19.0"
  val FastParseVersion  = "2.3.0"
  val CatsVersion       = "2.3.0"
  val CatsEffectVersion = "2.3.0"
  val MouseVersion      = "0.26.2"
  val GuavaVersion      = "30.0-jre"
  val DiffxVersion      = "0.3.30"

  val catsDependencies = Seq(
    "org.typelevel" %% "cats-core"   % CatsVersion,
    "org.typelevel" %% "cats-effect" % CatsEffectVersion,
    "org.typelevel" %% "mouse"       % MouseVersion
  )

  private val miscDependencies = Seq(
    "com.github.mpilquist" %% "simulacrum" % SimulacrumVersion,
    "com.lihaoyi"          %% "fastparse"  % FastParseVersion,
    "com.google.guava"     % "guava"       % GuavaVersion
  )

  private val testDependencies = Seq(
    "org.scalatest"          %% "scalatest"       % ScalaTestVersion % Test,
    "org.scalamock"          %% "scalamock"       % ScalaMockVersion % Test,
    "com.softwaremill.diffx" %% "diffx-scalatest" % DiffxVersion     % Test
  )

  val all: Seq[ModuleID] = Seq(
    testDependencies,
    miscDependencies,
    catsDependencies
  ).flatten

  val additionalResolvers: Seq[Resolver] = Seq(
    Resolver.jcenterRepo,
    Resolver.mavenCentral,
    "Typesafe Repo" at "https://repo.typesafe.com/typesafe/releases/"
  )

}
