import org.scalajs.linker.interface.ESVersion
import org.scalajs.linker.interface.ModuleSplitStyle
import sbt.ProjectOrigin.Organic
import sbtcrossproject.CrossProject

ThisBuild / scalaVersion := "3.4.2"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "net.apiduck"
ThisBuild / scalacOptions ++= Seq("-encoding", "utf-8", "-deprecation", "-feature", "-unchecked", "-Wunused:all", "-Wshadow:all")

lazy val core: CrossProject = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Full)
  .withoutSuffixFor(JVMPlatform)
  .in(file("modules/core"))
  .settings(
    name := "ducktape-core",
    autoCompilerPlugins := true,
    mainClass := Some("net.apiduck.ducktape.structure.Test"),
  )
  .jsConfigure(
    _.enablePlugins(ScalablyTypedConverterExternalNpmPlugin)
  )
  .jsSettings(
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "2.8.0",

    // Tell ScalablyTyped that we manage `bun install` ourselves
    externalNpm := baseDirectory.value / "../../../",
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
