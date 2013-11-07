name := "courserai"

version := "1.0"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  filters
)     

play.Project.playJavaSettings
