enablePlugins(ScalaJSPlugin)

name := "EP Sheet Root"

organization in ThisBuild := "com.lkroll.ep"

version in ThisBuild := "1.3.0"

scalaVersion in ThisBuild := "2.12.4"

resolvers += "Apache" at "http://repo.maven.apache.org/maven2"
resolvers += Resolver.bintrayRepo("lkroll", "maven")
resolvers += Resolver.mavenLocal

lazy val submitSheet = taskKey[Unit]("Submit the script that assembled and uploads the sheet");
lazy val submit = taskKey[Unit]("Assemble and fastOpt, and then upload the sheet");
lazy val submitSheetFull = taskKey[Unit]("Submit the script that assembled and uploads the sheet in fullOpt");
lazy val submitFull = taskKey[Unit]("Assemble and fullOpt, and then upload the sheet");

submitSheet := {
  s"./assemble.sc --version ${version.value}" !
}

submitSheetFull := {
  s"./assemble.sc --version ${version.value} --full true" !
}

lazy val root = project.in(file(".")).
  aggregate(epsheetJS, epsheetJVM).
  settings(
    publish := {},
    publishLocal := {},
    submit in Compile := Def.sequential(
      assembly in Compile in epsheetJVM,
      fastOptJS in Compile in epsheetJS,
      submitSheet in Compile
    ).value,
    submitFull in Compile := Def.sequential(
      assembly in Compile in epsheetJVM,
      fullOptJS in Compile in epsheetJS,
      submitSheetFull in Compile
    ).value
  )

lazy val epsheet = crossProject.in(file(".")).
  enablePlugins(BuildInfoPlugin).
  settings(
    name := "EP Sheet",
    libraryDependencies += "com.lihaoyi" %%% "scalatags" % "0.6.3",
    libraryDependencies += "com.lkroll.roll20" %%% "roll20-sheet-framework" % "0.6.+", 
    libraryDependencies += "com.lkroll.ep" %%% "ep-model" % "1.3.0",
    libraryDependencies += "org.scalatest" %%% "scalatest" % "3.+" % "test",
    EclipseKeys.useProjectId := true,
    EclipseKeys.eclipseOutput := Some("./etarget"),
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage := "com.lkroll.ep.sheet"
    //EclipseKeys.createSrc := EclipseCreateSrc.Default + EclipseCreateSrc.Managed
  ).
  jvmSettings(
    // Add JVM-specific settings here
    //name := "EP Sheet JVM",
    mainClass in assembly := Some("com.lkroll.roll20.sheet.Packager"),
    libraryDependencies += "org.scala-js" %% "scalajs-stubs" % scalaJSVersion % "provided"
  ).
  jsSettings(
    // Add JS-specific settings here
    //name := "EP Sheet JS",
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.1",
    libraryDependencies += "com.lkroll.roll20" %%% "roll20-sheet-facade" % "1.+" % "provided"
  )

lazy val epsheetJVM = epsheet.jvm
lazy val epsheetJS = epsheet.js