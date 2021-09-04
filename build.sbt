name := "scalaproto"

addCompilerPlugin(scalafixSemanticdb)
// enablePlugins(GraalVMNativeImagePlugin)

scalaVersion := "2.13.5"
version := "0.0"
lazy val `scalaproto` = (project in file("."))
  .settings(
    organization := "com.github.jakdar",
    name := "com.github.jakdar.scalaproto",
    resolvers ++= Dependencies.additionalResolvers,
    libraryDependencies ++= Dependencies.all,
    scalacOptions ++= CompilerOps.all,
    Test / parallelExecution := false,
  )
// graalVMNativeImageOptions ++= {
//   List(
//     // "-H:+ReportUnsupportedElementsAtRuntime",
//     // "--initialize-at-build-time",
//     // "--no-server",
//     "--enable-https",
//     "-H:EnableURLProtocols=http,https",
//     "--enable-all-security-services",
//     // "--no-fallback",
//     "--allow-incomplete-classpath",
//     "-H:+ReportExceptionStackTraces",
//   )
// }

assembly / test := {}
assembly / assemblyJarName := "scalaproto.jar"
