pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Podcaster"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":app")
include(":core:model")
include(":core:database")
include(":core:database-test-fixtures")
include(":core:logger")
include(":core:logger-test-fixtures")
include(":core:network")
include(":core:network-test-fixtures")
include(":core:data")
include(":core:sync")
