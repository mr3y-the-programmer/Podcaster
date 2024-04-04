plugins {
    alias(libs.plugins.podcaster.android.lib)
    alias(libs.plugins.sqldelight)
}

android {
    namespace = "com.mr3y.podcaster.core.local.di"
}

sqldelight {
    databases {
        create("PodcasterDatabase") {
            packageName.set("com.mr3y.podcaster.test") // This must be different from packageName set in database module
            dependency(projects.core.database)
        }
    }
}

dependencies {

    implementation(projects.core.model)
    implementation(projects.core.database)
    implementation(libs.sqldelight.sqlitedriver)
    implementation(libs.sqldelight.primitiveadapters)
    implementation(libs.sqlite.jdbc) {
        version { strictly(libs.versions.sqlite.jdbc.get()) }
    }
}
