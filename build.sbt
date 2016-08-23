organization := "me.yzhi"

name := "scala-macro-learning"

version := "1.0.0"

val commonSettings = Seq(
  scalaVersion := "2.11.0",
  scalacOptions ++= Seq("-deprecation", "-feature"),
  resolvers += Resolver.sonatypeRepo("releases"),
  addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),
  libraryDependencies ++= Seq(
    "org.scala-lang" % "scala-reflect" % scalaVersion.value,
    "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.1",
    "org.specs2" %% "specs2" % "2.3.12" % "test"
  )
)

lazy val blockLib        = project.in(file("block/lib")).settings(commonSettings : _*)

lazy val block           = project.in(file("block/app")).settings(commonSettings : _*).dependsOn(blockLib)
