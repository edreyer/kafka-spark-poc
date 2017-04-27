lazy val spark_gps_consumer = (project in file("."))
  .settings(
    name         := "spark_gps_consumer",
    organization := "io.liquidsoftware",
    scalaVersion := "2.11.11",
    version      := "0.1.0-SNAPSHOT"
  )

libraryDependencies ++= Seq(
  "org.apache.spark" % "spark-core_2.11" % "2.1.0",
  "org.apache.spark" % "spark-streaming_2.11" % "2.1.0",
  "org.apache.spark" % "spark-streaming-kafka-0-10_2.11" % "2.1.0",
  "org.apache.spark" % "spark-sql-kafka-0-10_2.11" % "2.1.0"

)
