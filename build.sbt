ThisBuild / scalaVersion := "3.1.1"
ThisBuild / wartremoverErrors ++= Warts.all

enablePlugins(GitHubPagesPlugin)
enablePlugins(SiteScaladocPlugin)

lazy val startupTransition: State => State = "writeHooks" :: _

lazy val root = (project in file("."))
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
      "org.scalatest" %% "scalatest" % "3.2.12" % Test
    )
  )

gitHubPagesSiteDir := baseDirectory.value / "target/site"
