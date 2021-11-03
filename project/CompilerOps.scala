object CompilerOps {
  // format: off
  val all: Seq[String] = Seq(
    // "-Xsemanticdb",
    // "-explain-types",                     // Explain type errors in more detail.
    // "-unchecked",                        // Enable additional warnings where generated code depends on assumptions.

    "-encoding", "utf-8",                // Specify character encoding used by source files.
    "-feature",                          // Emit warning and location for usages of features that should be imported explicitly.
    "-language:existentials",            // Existential types (besides wildcard types) can be written and inferred
    "-language:experimental.macros",     // Allow macro definition (besides implementation and application)
    "-language:higherKinds",             // Allow higher-kinded types
    "-language:implicitConversions",     // Allow definition of implicit functions called views
    // "-Ycheck-init", // TODO:bcm                        // Wrap field accessors to throw an exception on uninitialized access.
    "-deprecation",                // Emit warning and location for usages of deprecated APIs.
    // "-Xlint:adapted-args",               // Warn if an argument list is modified to match the receiver.
    // "-Xlint:constant",                   // Evaluation of a constant arithmetic expression results in an error.
    // "-Xlint:delayedinit-select",         // Selecting member of DelayedInit.
    // "-Xlint:doc-detached",               // A Scaladoc comment appears to be detached from its element.
    // "-Xlint:inaccessible",               // Warn about inaccessible types in method signatures.
    // "-Xlint:infer-any",                  // Warn when a type argument is inferred to be `Any`.
    // "-Xlint:missing-interpolator",       // A string literal appears to be missing an interpolator id.
    // "-Xlint:nullary-unit",               // Warn when nullary methods return Unit.
    // "-Xlint:option-implicit",            // Option.apply used implicit view.
    // "-Xlint:package-object-classes",     // Class or object defined in package object.
    // "-Xlint:poly-implicit-overload",     // Parameterized overloaded implicit methods are not visible as view bounds.
    // "-Xlint:private-shadow",             // A private field (or class parameter) shadows a superclass field.
    // "-Xlint:stars-align",                // Pattern sequence wildcard must align with sequence component.
    // "-Xlint:type-parameter-shadow",      // A local type parameter shadows a type already in scope.
    // "-Ywarn-dead-code",                  // Warn when dead code is identified.
    // "-Ywarn-extra-implicit", // TODO:bcm             // Warn when more than one implicit parameter section is defined.
    // "-Ywarn-numeric-widen",              // Warn when numerics are widened.
    "-Ywarn-unused",           // Warn if an implicit parameter is unused.
    // "-Ywarn-unused:implicits",           // Warn if an implicit parameter is unused.
    // "-Ywarn-unused:imports",             // Warn if an import selector is not referenced.
    // "-Ywarn-unused:locals",              // Warn if a local definition is unused.
    // "-Ywarn-unused:params",              // Warn if a value parameter is unused.
    // "-Ywarn-unused:patvars",             // Warn if a variable bound in a pattern is unused.
    // "-Ywarn-unused:privates",            // Warn if a private member is unused.
    // "-Ywarn-value-discard",              // Warn when non-Unit expression results are unused.
    // "-Ymacro-annotations"
  )
  // format: on

// [info] -Xlint:deprecation            -> -deprecation
// [info] -Xlint:adapted-args           -> X
// [info] -Xlint:constant               -> X
// [info] -Xlint:delayedinit-select     -> X
// [info] -Xlint:doc-detached           -> X
// [info] -Xlint:inaccessible           -> X
// [info] -Xlint:infer-any              -> X
// [info] -Xlint:missing-interpolator   -> X
// [info] -Xlint:nullary-unit           -> X
// [info] -Xlint:option-implicit        -> X
// [info] -Xlint:package-object-classes -> X
// [info] -Xlint:poly-implicit-overload -> X
// [info] -Xlint:private-shadow         -> X
// [info] -Xlint:stars-align            -> X
// [info] -Xlint:type-parameter-shadow  -> X
// [info] -Ywarn-dead-code              -> X
// [info] -Ywarn-numeric-widen          -> X

// [info] -Ywarn-unused:implicits       -> X
// [info] -Ywarn-unused:imports         -> X
// [info] -Ywarn-unused:locals          -> X
// [info] -Ywarn-unused:params          -> X
// [info] -Ywarn-unused:patvars         -> X
// [info] -Ywarn-unused:privates        -> X
// [info] -Ywarn-value-discard          -> X
// [info] -Ymacro-annotations           -> X
// [info] -explaintypes                 -> -explain-types
// [info] -Xcheckinit                   -> -Ycheck-init
}
