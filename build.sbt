ThisBuild / scalaVersion := "3.1.1"
ThisBuild / wartremoverErrors ++= Warts.all

enablePlugins(GhpagesPlugin)
enablePlugins(SiteScaladocPlugin)

lazy val startupTransition: State => State = "writeHooks" :: _

lazy val root = (project in file("."))
  .settings(
    name := "PPS-22-virsim",
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
      "org.scalatest" %% "scalatest" % "3.2.11" % Test
    )
  )

git.remoteRepo := "https://github.com/VirusSpreadSimulator/PPS-22-virsim.git"
