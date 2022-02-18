pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

val projectName = "translationloader"
rootProject.name = "$projectName-parent"

sequenceOf(
    "api",
).forEach {
    include("$projectName-$it")
    project(":$projectName-$it").projectDir = file(it)
}
