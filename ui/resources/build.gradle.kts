plugins {
    alias(libs.plugins.podcaster.android.lib)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.mr3y.podcaster.ui.resources"

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
}

ksp {
    arg("lyricist.packageName", "com.mr3y.podcaster")
}

dependencies {
    implementation(platform(libs.compose.bom.alpha))
    implementation(libs.ui)
    ksp(libs.lyricist.processor)
    implementation(libs.lyricist)
}
