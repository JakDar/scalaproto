object Dependencies {
  import sbt._
  val ScalaTestVersion  = "3.0.8"
  val ScalaMockVersion  = "4.4.0"
  val SimulacrumVersion = "0.19.0"
  val FastParseVersion  = "2.2.2"
  val CatsVersion       = "2.1.0"
  val CatsEffectVersion = "2.0.0"
  val MouseVersion      = "0.23"
  val GuavaVersion      = "28.2-jre"

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
    "org.scalatest" %% "scalatest" % ScalaTestVersion % Test,
    "org.scalamock" %% "scalamock" % ScalaMockVersion % Test
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
