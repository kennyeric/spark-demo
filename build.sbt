import sbt._
import Process._
import Keys._
import AssemblyKeys._

assemblySettings

lazy val commonSettings = Seq(
  name := "spark-demo",
  version := "1.0",
  scalaVersion := "2.10.5"
)

lazy val root = (project in file(".")).
  settings(commonSettings: _*)

libraryDependencies += "org.apache.spark" %% "spark-core" % "1.6.1" % "provided" 
libraryDependencies += "org.apache.spark" % "spark-streaming_2.10" % "1.6.1"
libraryDependencies += "org.apache.spark" % "spark-streaming-kafka_2.10" % "1.6.1"

mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
  {
    //copy paste from spark/project/SparkBuild.scala
    case PathList("org", "datanucleus", xs @ _*)             => MergeStrategy.discard
    case m if m.toLowerCase.endsWith("manifest.mf")          => MergeStrategy.discard
    case m if m.toLowerCase.matches("meta-inf.*\\.sf$")      => MergeStrategy.discard
    case "log4j.properties"                                  => MergeStrategy.discard
    case m if m.toLowerCase.startsWith("meta-inf/services/") => MergeStrategy.filterDistinctLines
    case "reference.conf"                                    => MergeStrategy.concat
    case _                                                   => MergeStrategy.first
  }
}
