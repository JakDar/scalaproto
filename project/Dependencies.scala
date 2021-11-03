object Dependencies {
  import sbt._
  val CatsParseVersion = "0.3.4"
  val CatsVersion      = "2.6.1"
  val MouseVersion     = "1.0.7"
  val GuavaVersion     = "30.1.1-jre"
  val ScalametaVersion = "4.4.30"

  val catsDependencies = Seq(
    "org.typelevel" %% "cats-core" % CatsVersion cross CrossVersion.for3Use2_13,
    "org.typelevel" %% "mouse"     % MouseVersion cross CrossVersion.for3Use2_13,
  )

  private val miscDependencies = Seq(
    "org.typelevel"   %% "cats-parse" % CatsParseVersion cross CrossVersion.for3Use2_13,
    "com.google.guava" % "guava"      % GuavaVersion,
    "org.scalameta"   %% "scalameta"  % ScalametaVersion cross CrossVersion.for3Use2_13,
    "com.lihaoyi"     %% "ujson"      % "1.4.2" cross CrossVersion.for3Use2_13,
    "com.lihaoyi"     %% "pprint"     % "0.6.6" cross CrossVersion.for3Use2_13,
  )

  private val testDependencies = Seq(
    "org.scalameta" %% "munit"     % "0.7.29"         % Test,
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
