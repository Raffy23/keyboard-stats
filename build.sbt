name := "keyboard-stats"
version := "0.1"
scalaVersion := "2.12.6"

resourceDirectory in Compile := baseDirectory.value / "resources"

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)

libraryDependencies ++= Seq(
  // JNA & System hooks for Windows
  "net.java.dev.jna" % "jna" % "4.5.1",
  "net.java.dev.jna" % "jna-platform" % "4.5.1",

  // SVG, XML processing
  "org.apache.xmlgraphics" % "batik-dom" % "1.10",
  "org.scala-lang.modules" %% "scala-xml" % "1.1.0",

  // JavaFX for UI
  "org.scalafx" %% "scalafxml-core-sfx8" % "0.4",
  "org.scalafx" %% "scalafx" % "8.0.144-R12"
)