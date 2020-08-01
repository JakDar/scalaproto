enablePlugins(SbtNativePackager)
enablePlugins(JavaAppPackaging)
enablePlugins(GraalVMNativeImagePlugin)

addCompilerPlugin(scalafixSemanticdb)

scalaVersion := "2.13.3"
version := "0.0"
lazy val `scalaproto` = (project in file("."))
  .settings(
    organization := "com.github.jakdar",
    name := "com.github.jakdar.scalaproto",
    scalaVersion := "2.13.3",
    resolvers ++= Dependencies.additionalResolvers,
    libraryDependencies ++= Dependencies.all,
    scalacOptions ++= CompilerOps.all,
    parallelExecution in Test := false
  )
graalVMNativeImageOptions ++= {
  List(
    "-H:+ReportUnsupportedElementsAtRuntime",
    "--initialize-at-build-time",
    "--no-server",
    "--enable-https",
    "-H:EnableURLProtocols=http,https",
    "--enable-all-security-services",
    "--no-fallback",
    "--allow-incomplete-classpath",
    "-H:+ReportExceptionStackTraces"
  )
}
