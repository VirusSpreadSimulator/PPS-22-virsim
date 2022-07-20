ThisBuild / scalaVersion := "3.1.1"
ThisBuild / wartremoverErrors ++= Warts.all

lazy val root = (project in file("."))
  .settings(
    name := "PPS-22-virsim"
  )

onLoad in Global ~= (_ andThen ("writeHooks" :: _))
