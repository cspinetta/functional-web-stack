
val artifactId = "demo"

organization := "com.despegar.demo"
name := artifactId
scalaVersion := "2.12.6"

fork in run := true
parallelExecution in test := false

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

val Http4sVersion   = "0.18.11"
val CirceVersion    = "0.9.3"
val DoobieVersion   = "0.5.3"

libraryDependencies ++= Seq(
  "org.http4s"              %%  "http4s-blaze-server"     % Http4sVersion,
  "org.http4s"              %%  "http4s-circe"            % Http4sVersion,
  "org.http4s"              %%  "http4s-dsl"              % Http4sVersion,
  "org.http4s"              %%  "http4s-blaze-client"     % Http4sVersion,
  "org.tpolecat"            %%  "doobie-core"             % DoobieVersion,
  "org.tpolecat"            %%  "doobie-h2"               % DoobieVersion             % "test",
  "ch.qos.logback"          %   "logback-classic"         % "1.2.1",
  "com.github.pureconfig"   %%  "pureconfig"              % "0.7.1",
  "org.mariadb.jdbc"        %   "mariadb-java-client"     % "1.5.9",
  "com.zaxxer"              %   "HikariCP"                % "2.6.2",
  "io.circe"                %%  "circe-generic"           % CirceVersion,
  "io.circe"                %%  "circe-java8"             % CirceVersion,
  "io.circe"                %%  "circe-generic-extras"    % CirceVersion,
  "org.scalatest"           %   "scalatest_2.12"          % "3.0.3"                   % "test",
  "org.mockito"             %   "mockito-core"            % "2.7.22"                  % "test"
)

mainClass := Some("com.despegar.demo.Server")

enablePlugins(BuildInfoPlugin)
buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion)
buildInfoPackage := "com.despegar.demo"

//resolvers ++= Seq(
//  Resolver.sonatypeRepo("releases")
//)

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
