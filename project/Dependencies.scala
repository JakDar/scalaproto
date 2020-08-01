object Dependencies {
  import sbt._
  val ScalaTestVersion  = "3.2.0"
  val ScalaMockVersion  = "5.0.0"
  val SimulacrumVersion = "0.19.0"
  val FastParseVersion  = "2.2.2"
  val CatsVersion       = "2.1.1"
  val CatsEffectVersion = "2.1.4"
  val MouseVersion      = "0.25"
  val GuavaVersion      = "29.0-jre"
  val DiffxVersion      = "0.3.29"

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
