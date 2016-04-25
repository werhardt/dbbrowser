name := """dbbrowser"""

version := "1.0"

lazy val `dbbrowser` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

routesGenerator := InjectedRoutesGenerator

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

libraryDependencies ++= Seq(jdbc, cache, ws, filters,
  specs2 % Test

)

unmanagedResourceDirectories in Test <+= baseDirectory(_ / "target/web/public/test")

libraryDependencies += "com.adrianhurt" %% "play-bootstrap" % "1.0-P25-B3"

libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "1.0.1"

libraryDependencies += "com.github.t3hnar" % "scala-bcrypt_2.11" % "2.5"


skip in update := true
//fork in run := true