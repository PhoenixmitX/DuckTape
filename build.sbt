import org.scalajs.linker.interface.ESVersion
import org.scalajs.linker.interface.ModuleSplitStyle
import sbt.ProjectOrigin.Organic
import sbtcrossproject.CrossProject

ThisBuild / scalaVersion := "3.4.2"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "net.apiduck"
ThisBuild / scalacOptions ++= Seq("-encoding", "utf-8", "-deprecation", "-feature", "-unchecked", "-Wunused:all", "-Wshadow:all")

val PekkoVersion = "1.0.2"

lazy val core: CrossProject = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Full)
  .withoutSuffixFor(JVMPlatform)
  .in(file("modules/core"))
  .settings(
    name := "ducktape-core",
  )
  .jsConfigure(
    _.enablePlugins(ScalablyTypedConverterExternalNpmPlugin)
  )
  .jsSettings(
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "2.8.0",

    // Tell ScalablyTyped that we manage `bun install` ourselves
    externalNpm := baseDirectory.value / "../../../",
  )

lazy val messaging: CrossProject = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Full)
  .withoutSuffixFor(JVMPlatform)
  .in(file("modules/messaging"))
  .dependsOn(core)
  .settings(
    name := "ducktape-messaging",
    libraryDependencies ++= Seq(
      "io.bullet" %%% "borer-core" % "1.14.0",
      "io.bullet" %%% "borer-derivation" % "1.14.0",
    ),
  )

lazy val pekkoMessaging: Project = project
  .in(file("modules/pekko-messaging"))
  .dependsOn(messaging.jvm)
  .settings(
    name := "ducktape-pekko-messaging",
    libraryDependencies += "org.apache.pekko" %% "pekko-actor-typed" % PekkoVersion,
  )

lazy val demo: Project = project
  .in(file("modules/demo"))
  .dependsOn(core.js)
  .enablePlugins(ScalaJSPlugin)
  .settings(
    name := "ducktape-demo",
    scalaJSUseMainModuleInitializer := true,

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
