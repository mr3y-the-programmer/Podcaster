import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

group = "com.mr3y.podcaster.conventionplugins"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ktlint.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "podcaster.android.application"
            implementationClass = "com.mr3y.podcaster.gradle.AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "podcaster.android.library"
            implementationClass = "com.mr3y.podcaster.gradle.AndroidLibraryConventionPlugin"
        }
        register("jvmLibrary") {
            id = "podcaster.jvm.library"
            implementationClass = "com.mr3y.podcaster.gradle.JvmConventionPlugin"
        }
    }
}

