object Dependencies {
  import sbt._
  val CatsParseVersion = "0.3.7"
  val CatsVersion      = "2.7.0"
  val MouseVersion     = "1.1.0"
  val GuavaVersion     = "31.1-jre"
  val ScalametaVersion = "4.5.9"

  val catsDependencies = Seq(
    "org.typelevel" %% "cats-core" % CatsVersion,
    "org.typelevel" %% "mouse"     % MouseVersion,
  )

  private val miscDependencies = Seq(
    "org.typelevel"   %% "cats-parse" % CatsParseVersion,
    "com.google.guava" % "guava"      % GuavaVersion,
    "org.scalameta"   %% "scalameta"  % ScalametaVersion cross CrossVersion.for3Use2_13,
    "com.lihaoyi"     %% "ujson"      % "2.0.0" cross CrossVersion.for3Use2_13,
    "com.lihaoyi"     %% "pprint"     % "0.7.3" cross CrossVersion.for3Use2_13,
  )

  private val testDependencies = Seq(
    "org.scalameta" %% "munit" % "0.7.29" % Test
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
