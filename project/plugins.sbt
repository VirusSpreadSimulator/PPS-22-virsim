//scalafmt plugin
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.4.6")

//wartremover plugin
addSbtPlugin("org.wartremover" % "sbt-wartremover" % "3.0.5")

//sbt-jacoco plugin
addSbtPlugin("com.github.sbt" % "sbt-jacoco" % "3.4.0")

//sbt git hooks plugin
addSbtPlugin("uk.co.randomcoding" % "sbt-git-hooks" % "0.2.0")

//site generator
addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "1.4.1")
addSbtPlugin("com.github.sbt" % "sbt-unidoc" % "0.5.0")

//publish pages
addSbtPlugin("io.kevinlee" % "sbt-github-pages" % "0.10.0")

//generate FAT Jar
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "1.2.0")

addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.1.0")

addSbtPlugin("org.portable-scala" % "sbt-scala-native-crossproject" % "1.2.0")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.10.1")

addSbtPlugin("org.scala-native" % "sbt-scala-native" % "0.4.5")
