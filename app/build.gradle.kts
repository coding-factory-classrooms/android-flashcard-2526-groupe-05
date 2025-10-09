plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.flashcard"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.flashcard"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "v13.7.42-beta.9+build.2025.10.08.1337-preRC9-hotfix-rev12-patch.666-devbuild+exp.sha.5114f859.9.9.9-alpha42-debug-extra.unstable"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation("androidx.cardview:cardview:1.0.0")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("com.google.code.gson:gson:2.13.2")
    implementation(libs.recyclerview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}