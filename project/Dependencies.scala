import sbt._

object Dependencies {
  
  val resolutionRepos = Seq(
    "Sonatype OSS Releases"   at "http://oss.sonatype.org/content/repositories/releases/",
    "Typesafe"                at "http://repo.typesafe.com/typesafe/releases/",
    "Artima Maven Repository" at "http://repo.artima.com/releases",
    "Confluent"               at "http://packages.confluent.io/maven/"
  )

  object V {
    val avro      = "1.8.2"
    val beachape  = "1.5.13"
    val confluent = "4.0.0"
    val circe     = "0.9.3"
    val logback   = "1.2.3"
    val logging   = "3.9.2"
    val kafka     = "1.1.1"

    // Test
    val scalatest = "3.0.5"
  }

  val Libraries = Seq(
    "ch.qos.logback"              %   "logback-classic"               % V.logback,
    "com.beachape"                %%  "enumeratum"                    % V.beachape,
    "com.typesafe.scala-logging"  %%  "scala-logging"                 % V.logging,
    "io.confluent"                %   "kafka-avro-serializer"         % V.confluent,
    "io.confluent"                %   "kafka-schema-registry-client"  % V.confluent,
    "io.circe"                    %%  "circe-core"                    % V.circe,
    "io.circe"                    %%  "circe-generic"                 % V.circe,
    "io.circe"                    %%  "circe-parser"                  % V.circe,
    "org.apache.avro"             %   "avro"                          % V.avro,
    "org.apache.kafka"            %%  "kafka"                         % V.kafka,

    // Test
    "org.scalatest"               %%  "scalatest"                     % V.scalatest % "test"
  )
}
