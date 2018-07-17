name := "hInput-Heatmap"
version := "0.1"
scalaVersion := "2.12.6"

resourceDirectory in Compile := baseDirectory.value / "resources"

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)

libraryDependencies ++= Seq(
  // JNA & System hooks for platform specific stuff (winapi, x11, ....)
  "net.java.dev.jna" % "jna" % "4.5.1",
  "net.java.dev.jna" % "jna-platform" % "4.5.1",

  // SVG, XML processing
 "org.apache.xmlgraphics" % "batik-codec" % "1.10",
  "org.apache.xmlgraphics" % "batik-transcoder" % "1.10",
  "org.scala-lang.modules" %% "scala-xml" % "1.1.0",

  // JavaFX for UI
  "org.scalafx" %% "scalafxml-core-sfx8" % "0.4",
  "org.scalafx" %% "scalafx" % "8.0.144-R12",
  "com.jfoenix" % "jfoenix" % "8.0.5",
  "de.jensd" % "fontawesomefx" % "8.9",

  // misc stuff
  "com.github.pureconfig" %% "pureconfig" % "0.9.0"
)

val circeVersion = "0.9.3"
libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

assemblyMergeStrategy in assembly := {
  case PathList("org", "w3c", "dom", xs @ _*) => MergeStrategy.first
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}