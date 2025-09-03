import org.jetbrains.kotlin.gradle.dsl.JvmTarget

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.aboutLibrariesPlugin)
    alias(libs.plugins.devtoolsKsp)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.example.guitartuner"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.guitartuner"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
        }
        create("profile") {
            initWith(getByName("debug"))
            isDebuggable = false
            isProfileable = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_1_8
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    androidResources {
        generateLocaleConfig = true
    }
}

configurations.all {
    resolutionStrategy {
        force(
            "org.jetbrains.kotlinx:kotlinx-coroutines-core:${libs.versions.kotlinxCoroutines.get()}",
            "org.jetbrains.kotlinx:kotlinx-coroutines-android:${libs.versions.kotlinxCoroutines.get()}"
        )
    }
}

dependencies {

    // Android & Compose
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.animation)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.material.ripple)
    implementation(libs.androidx.material3.window)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.constraintlayout.compose)

    // Coroutines pinned for Satchel compatibility
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Open Source Licenses
    implementation(libs.aboutlibraries.core)
    implementation(libs.aboutlibraries.compose)
    implementation(libs.aboutlibraries)

    // Utility
    implementation(libs.theme)
    implementation(libs.preview)

    // Audio
    implementation(files("libs/TarsosDSP-Android.jar"))
    implementation(libs.audio.mididriver) {
        version { branch = "master" }
    }

    // Other
    implementation(libs.accompanist.adaptive)
    implementation(libs.androidx.lifecycle.viewModelCompose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.navigation.compose)

    // Data Storage
    implementation(libs.satchel.core)
    implementation(libs.androidx.material3.android)

    // Room
    annotationProcessor(libs.androidx.room.compiler)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx) // Kotlin Extensions and Coroutines support
    testImplementation(libs.androidx.room.testing) // Test helpers
    implementation(libs.androidx.room.paging)  // Paging 3 Integration

    // DI
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.compose)
    implementation(libs.koin.androidx.compose.navigation)
    implementation(libs.koin.core)
    implementation(libs.koin.core.coroutines)
    implementation(libs.koin.android)
    implementation(libs.koin.android.compat)
    implementation(libs.koin.androidx.workmanager)
    implementation(libs.koin.androidx.navigation)

    // Testing
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}