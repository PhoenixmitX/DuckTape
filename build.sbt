import org.scalajs.linker.interface.ESVersion
import org.scalajs.linker.interface.ModuleSplitStyle
import sbt.ProjectOrigin.Organic
import sbtcrossproject.CrossProject

ThisBuild / scalaVersion := "3.9.0"
ThisBuild / version := "0.0.6"
ThisBuild / versionScheme := Some("early-semver")
ThisBuild / organization := "net.apiduck"
ThisBuild / scalacOptions ++= Seq("-encoding", "utf-8", "-deprecation", "-feature", "-unchecked", "-Wunused:all", "-Wshadow:all", "-Yexplicit-nulls")

val PekkoVersion = "1.7.1"
val BorerVersion = "1.19.0"
val ScalaJsDomVersion = "2.8.1"

lazy val core: CrossProject = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Full)
  .withoutSuffixFor(JVMPlatform)
  .in(file("modules/core"))
  .settings(
    name := "ducktape-core",
  )
  .jsSettings(
    // the signal-polyfill npm package is used through a hand-written facade (web/signals/SignalPolyfill.scala)
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % ScalaJsDomVersion,
  )

lazy val messaging: CrossProject = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Full)
  .withoutSuffixFor(JVMPlatform)
  .in(file("modules/messaging"))
  .dependsOn(core)
  .settings(
    name := "ducktape-messaging",
    scalacOptions += "-Xcheck-macros",
    libraryDependencies ++= Seq(
      "io.bullet" %%% "borer-core" % BorerVersion,
      "io.bullet" %%% "borer-derivation" % BorerVersion,
    ),
    publish / skip := true, // not ready for publishing
  )

lazy val pekkoMessaging: Project = project
  .in(file("modules/pekko-messaging"))
  .dependsOn(messaging.jvm)
  .settings(
    name := "ducktape-pekko-messaging",
    libraryDependencies += "org.apache.pekko" %% "pekko-actor-typed" % PekkoVersion,
    publish / skip := true, // not ready for publishing
  )

lazy val demo: Project = project
  .in(file("modules/demo"))
  .dependsOn(core.js)
  .enablePlugins(ScalaJSPlugin)
  .settings(
    name := "ducktape-demo",
    scalaJSUseMainModuleInitializer := true,
    publish / skip := true,

    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.ESModule)
      .withESFeatures(_
        .withESVersion(ESVersion.ES2021)
        .withAvoidClasses(false)
        .withAllowBigIntsForLongs(true)
        // generates smaller bundles if we keep avoidLetsAndConsts on
        // .withAvoidLetsAndConsts(false)
      )
      // small modules are counterproductive
      // .withModuleSplitStyle(ModuleSplitStyle.SmallModulesFor(List("net.apiduck")))
    },
  )

// Publishing
publish / skip := true
val GITHUB_TOKEN = sys.env.get("GITHUB_TOKEN") // .getOrElse(throw new Exception("GITHUB_TOKEN is not set"))
if (GITHUB_TOKEN.isDefined) {
  Seq(
    ThisBuild / publishTo := Some("GitHub Package Registry" at "https://maven.pkg.github.com/phoenixmitx/ducktape"),
    ThisBuild / credentials += Credentials("GitHub Package Registry", "maven.pkg.github.com", "phoenixmitx", GITHUB_TOKEN.get),
  )
} else Seq.empty
