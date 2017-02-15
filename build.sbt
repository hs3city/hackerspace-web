name := "hackerspace-web"

version := "3.0.1"

scalaVersion := "2.11.7"

resolvers := ("Atlassian Releases" at "https://maven.atlassian.com/public/") +: resolvers.value

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies ++= Seq(
  cache,
  evolutions,
  filters,
  "mysql" % "mysql-connector-java" % "5.1.24",
  "com.mohiva" %% "play-silhouette" % "4.0.0",
  "com.mohiva" %% "play-silhouette-password-bcrypt" % "4.0.0",
  "com.mohiva" %% "play-silhouette-persistence" % "4.0.0",
  "com.mohiva" %% "play-silhouette-crypto-jca" % "4.0.0",
  "org.webjars" %% "webjars-play" % "2.5.0",
  "net.codingwell" %% "scala-guice" % "4.0.0",
  "com.iheart" %% "ficus" % "1.4.0",
  "com.adrianhurt" %% "play-bootstrap" % "1.1-P25-B3",
  specs2 % Test,
  "com.typesafe.play" %% "play-slick" % "2.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "2.0.0",
  "com.github.tototoshi" %% "slick-joda-mapper" % "2.2.0",
  "joda-time" % "joda-time" % "2.7",
  "org.joda" % "joda-convert" % "1.7",
  "com.h2database" % "h2" % "1.4.188",

  "org.webjars" % "bootstrap" % "3.3.7",
  "org.webjars" % "metisMenu" % "1.1.3",
  "org.webjars" % "morrisjs" % "0.5.1",
  "org.webjars" % "font-awesome" % "4.3.0",
  "org.webjars" % "jquery" % "2.2.4",
  "org.webjars" % "flot" % "0.8.3",
  "org.webjars" % "datatables" % "1.10.5",
  "org.webjars" % "datatables-plugins" % "1.10.5",
  "org.webjars" % "bootstrap-datepicker" % "1.4.0"

)

lazy val root = (project in file(".")).enablePlugins(PlayScala)

routesGenerator := InjectedRoutesGenerator

includeFilter in (Assets, LessKeys.less) := "sb-admin-2.less"

scalacOptions ++= Seq(
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  "-unchecked", // Enable additional warnings where generated code depends on assumptions.
  "-Xfatal-warnings", // Fail the compilation if there are any warnings.
  "-Xlint", // Enable recommended additional warnings.
  "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver.
  "-Ywarn-dead-code", // Warn when dead code is identified.
  "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
  "-Ywarn-nullary-override", // Warn when non-nullary overrides nullary, e.g. def foo() over def foo.
  "-Ywarn-numeric-widen" // Warn when numerics are widened.
)
