import com.despegar.sbt.madonna.Madonna.MadonnaKeys
import com.despegar.sbt.madonna.Madonna.MadonnaKeys.{healthCheckPort, healthCheckProtocol, healthCheckTimeout, healthCheckURI}
import com.despegar.sbt.madonna.options.HTTP

import sbtrelease.ReleasePlugin.autoImport.{ReleaseStep, _}
import sbtrelease.ReleaseStateTransformations._

import scala.concurrent.duration._

val artifactId = "demo"

organization := "com.despegar.robotech"
name := artifactId
scalaVersion := "2.12.3"

fork in run := true

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-unchecked",
  "-Xlint",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Xfuture")

val Http4sVersion   = "0.17.2"
val CirceVersion    = "0.8.0"

libraryDependencies ++= Seq(
  "com.despegar.library"    %   "logging"                 % "0.0.3",
  "org.http4s"              %%  "http4s-blaze-server"     % Http4sVersion,
  "org.http4s"              %%  "http4s-circe"            % Http4sVersion,
  "org.http4s"              %%  "http4s-dsl"              % Http4sVersion,
  "org.http4s"              %%  "http4s-blaze-client"     % Http4sVersion,
  "org.tpolecat"            %%  "doobie-core-cats"        % "0.4.4",
  "ch.qos.logback"          %   "logback-classic"         % "1.2.1",
  "com.github.pureconfig"   %%  "pureconfig"              % "0.7.1",
  "org.mariadb.jdbc"        %   "mariadb-java-client"     % "1.5.9",
  "com.zaxxer"              %   "HikariCP"                % "2.6.2",
  "io.circe"                %%  "circe-generic"           % CirceVersion,
  "io.circe"                %%  "circe-java8"             % CirceVersion,
  "io.circe"                %%  "circe-generic-extras"    % CirceVersion,
  "com.h2database"          %   "h2"                      % "1.4.193",
  "com.despegar.tech"       %%  "http4s-scala-routing"    % "0.1.0",
  "joda-time"               %   "joda-time"               % "2.9.9",
  "com.h2database"          %   "h2"                      % "1.4.196"                 % "test",
  "org.scalatest"           %   "scalatest_2.12"          % "3.0.3"                   % "test",
  "org.mockito"             %   "mockito-core"            % "2.7.22"                  % "test"
)

mainClass := Some("com.despegar.demo.Server")

enablePlugins(BuildInfoPlugin)
enablePlugins(JavaAgent)

buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion)

buildInfoPackage := "com.despegar.demo"

resolvers ++= Seq(
  "Nexus" at "http://nexus.despegar.it:8080/nexus/content/groups/public/",
  "Nexus miami" at "http://nexus:8080/nexus/content/groups/public/",
  Resolver.sonatypeRepo("releases")
)

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)

publishTo := {
  if(version.value.endsWith("SNAPSHOT"))
    Some("Nexus snapshots" at "http://nexus.despegar.it:8080/nexus/content/repositories/snapshots/")
  else
    Some("Nexus releases" at "http://nexus.despegar.it:8080/nexus/content/repositories/releases/")
}

healthCheckProtocol := HTTP
healthCheckPort := 9290
healthCheckURI := "/health-check"
healthCheckTimeout := 180.seconds
publish := MadonnaKeys.tarPublish.value
publishLocal := MadonnaKeys.tarPublishLocal.value
MadonnaKeys.packageFilename := s"$artifactId-v${version.value}"

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  setNextVersion,
  commitNextVersion,
  pushChanges
)