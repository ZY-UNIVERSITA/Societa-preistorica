ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.7.1"

lazy val root = (project in file("."))
  .settings(
    name := "Societa preistorica",
    idePackagePrefix := Some("com.zy.societapreistorica")
  )

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.8.8"