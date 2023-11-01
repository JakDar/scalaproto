object Dependencies {
  import sbt._
  val CatsParseVersion = "0.3.9"
  val CatsVersion      = "2.10.0"
  val MouseVersion     = "1.2.1"
  val GuavaVersion     = "31.1-jre"
  val ScalametaVersion = "4.8.12"

  val catsDependencies = Seq(
    "org.typelevel" %% "cats-core" % CatsVersion,
    "org.typelevel" %% "mouse"     % MouseVersion,
  )

  private val miscDependencies = Seq(
    "org.typelevel"   %% "cats-parse" % CatsParseVersion,
    "com.google.guava" % "guava"      % GuavaVersion,
    "org.scalameta"   %% "scalameta"  % ScalametaVersion cross CrossVersion.for3Use2_13,
    "com.lihaoyi"     %% "ujson"      % "3.1.3" cross CrossVersion.for3Use2_13,
    "com.lihaoyi"     %% "pprint"     % "0.8.1" cross CrossVersion.for3Use2_13,
  )

  private val testDependencies = Seq(
    "org.scalameta" %% "munit" % "1.0.0-M10" % Test
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
