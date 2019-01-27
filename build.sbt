logLevel :=  Level.Info

lazy val root = project.in(file("."))
  .settings(
    organization  :=  "org.selwyn",
    name          :=  "kafka-loader",
    version       :=  "0.1.0",
    scalaVersion  :=  "2.12.7",
    description   :=  "Load template based records with randomized fields into Kafka",
    scalacOptions :=  BuildSettings.compilerOptions,
    scalacOptions in  (Compile, console) ~= { _.filterNot(Set("-Ywarn-unused-import")) },
    scalacOptions in  (Test, console)    := (scalacOptions in (Compile, console)).value,
    javacOptions  :=  BuildSettings.javaCompilerOptions,
    resolvers     ++= Dependencies.resolutionRepos,
    shellPrompt   :=  { _ => "kafka-loader> "}
  )
  .settings(BuildSettings.wartremoverSettings)
  .settings(BuildSettings.scalaFmtSettings)
  .settings(BuildSettings.scalifySettings)
  .settings(BuildSettings.sbtAssemblySettings)
  .settings(
    libraryDependencies ++= Dependencies.Libraries
  )
