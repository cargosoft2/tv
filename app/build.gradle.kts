plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.app.palestineapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.app.palestineapp"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.androidx.leanback)
    implementation(libs.glide)
    // Add ExoPlayer dependency
    implementation(libs.exoplayer)
}