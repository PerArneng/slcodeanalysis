

name := "slcodeanalysis"

version := "0.1"

scalaVersion := "2.12.4"

resolvers += Resolver.sonatypeRepo("public")

libraryDependencies += "com.github.scopt" %% "scopt" % "3.7.0"
libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "1.0.6"

libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.4"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % "test"

