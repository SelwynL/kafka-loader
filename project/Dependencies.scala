import sbt._

object Dependencies {
  val resolutionRepos = Seq(
    "Sonatype OSS Releases"   at "http://oss.sonatype.org/content/repositories/releases/",
    "Typesafe"                at "http://repo.typesafe.com/typesafe/releases/",
    "Artima Maven Repository" at "http://repo.artima.com/releases"
  )

  object V {
    val logback   = "1.2.3"
    val logging   = "3.9.2"

    // Test
    val scalatest = "3.0.5"
  }

  val Libraries = Seq(
    "ch.qos.logback"              % "logback-classic" % V.logback,
    "com.typesafe.scala-logging" %% "scala-logging"   % V.logging,

    // Test
    "org.scalatest"              %% "scalatest"       % V.scalatest % "test"
  )
}
