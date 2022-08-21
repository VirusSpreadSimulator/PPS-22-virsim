ThisBuild / scalaVersion := "3.1.1"
ThisBuild / wartremoverWarnings += Wart.Nothing
ThisBuild / wartremoverErrors += Wart.Nothing

lazy val startupTransition: State => State = "writeHooks" :: _

lazy val root = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Full)
  .settings(
    name := "PPS-22-virsim",
    assembly / assemblyJarName := "virsim.jar",
    Global / onLoad := {
      val old = (Global / onLoad).value
      startupTransition compose old
    },
    // add XML report for sonarcloud
    jacocoReportSettings := JacocoReportSettings(
      "Jacoco Coverage Report",
      None,
      JacocoThresholds(),
      Seq(JacocoReportFormats.ScalaHTML, JacocoReportFormats.XML),
      "utf-8"
    ),
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.2.13" % Test,
      "com.disneystreaming" %% "weaver-monix" % "0.6.15" % Test,
      "io.monix" %%% "monix" % "3.4.1",
      "dev.optics" %%% "monocle-core" % "3.1.0",
      "it.unibo.alice.tuprolog" % "tuprolog" % "3.3.0",
      "dev.optics" %%% "monocle-macro" % "3.1.0",
      "org.scala-lang" %% "scala3-staging" % scalaVersion.value,
      "org.virtuslab" %%% "scala-yaml" % "0.0.4",
      "org.jfree" % "jfreechart" % "1.5.3"
    ),
    testFrameworks += new TestFramework("weaver.framework.Monix")
  )
  .jsSettings(
    scalaJSUseMainModuleInitializer := true,
    libraryDependencies ++= Seq("org.scala-js" %%% "scalajs-dom" % "2.2.0")
  )
  .jvmSettings(
    libraryDependencies ++= Seq(
      "org.scala-lang.modules" %% "scala-swing" % "3.0.0"
    )
  )

lazy val aggregate = (project in file("."))
  .enablePlugins(ScalaUnidocPlugin)
  .enablePlugins(GitHubPagesPlugin)
  .enablePlugins(SiteScaladocPlugin)
  .aggregate(root.jvm, root.js)
  .settings(
    name := "PPS-22-Virsim",
    ScalaUnidoc / siteSubdirName := "latest/api/",
    addMappingsToSiteDir(ScalaUnidoc / packageDoc / mappings, ScalaUnidoc / siteSubdirName)
  )

gitHubPagesSiteDir := baseDirectory.value / "target/site"
