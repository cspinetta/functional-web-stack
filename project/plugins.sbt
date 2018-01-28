logLevel := Level.Warn

resolvers += "Typesafe repository" at "https://dl.bintray.com/typesafe/maven-releases/"
resolvers += "Nexus despegar" at "http://nexus.despegar.it:8080/nexus/content/groups/public/"
resolvers += "Nexus despegar miami" at "http://nexus:8080/nexus/content/groups/public/"

resolvers += Resolver.typesafeRepo("releases")

addSbtPlugin("com.lightbend.sbt" % "sbt-javaagent" % "0.1.3")

addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.7.0")
addSbtPlugin("com.despegar.sbt" %% "madonna" % "0.1.1")
addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.5")
