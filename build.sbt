ThisBuild / scalaVersion := "2.13.7"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "org.yuvalitzchakov"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "adam-check-list",
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.client3" %% "core" % "3.3.16",
      "com.softwaremill.sttp.client3" %% "httpclient-backend-zio" % "3.3.16",
      "com.softwaremill.sttp.client3" %% "async-http-client-backend-zio" % "3.3.16",
      "dev.zio" %% "zio" % "1.0.12",
      "dev.zio" %% "zio-test" % "1.0.12" % Test
    ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )
