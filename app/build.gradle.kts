plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "app.lumora.gallery"
    compileSdk = 36

    defaultConfig {
        applicationId = "app.lumora.gallery"

        minSdk = 26
        targetSdk = 35

        versionCode = 1
        versionName = "1.0.0"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    /**
     * ABI Split APK Support
     */
    splits {
        abi {
            isEnable = true
            reset()

            include(
                "arm64-v8a",
                "armeabi-v7a",
                "x86",
                "x86_64"
            )

            isUniversalApk = true
        }
    }

    /**
     * Build Types
     */
    buildTypes {

        debug {
            isMinifyEnabled = false
            isDebuggable = true
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile(
                    "proguard-android-optimize.txt"
                ),
                "proguard-rules.pro"
            )
        }
    }

    /**
     * Java + Kotlin
     */
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlin {
        jvmToolchain(21)
    }

    /**
     * Compose
     */
    buildFeatures {
        compose = true
        buildConfig = true
    }

    /**
     * Packaging
     */
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/LICENSE"
            excludes += "META-INF/LICENSE.txt"
            excludes += "META-INF/license.txt"
            excludes += "META-INF/NOTICE"
            excludes += "META-INF/NOTICE.txt"
            excludes += "META-INF/notice.txt"
        }
    }

    /**
     * APK Naming
     */
    applicationVariants.all {

        outputs.all {

            val abi =
                filters.find {
                    it.filterType.name == "ABI"
                }?.identifier ?: "universal"

            val version =
                versionName ?: "1.0.0"

            val appName = "LumoraGallery"

            val fileName =
                "${appName}-${abi}-v${version}.apk"

            @Suppress("DEPRECATION")
            (this as com.android.build.gradle.internal.api.BaseVariantOutputImpl)
                .outputFileName = fileName
        }
    }

    /**
     * Lint
     */
    lint {
        checkReleaseBuilds = false
        abortOnError = false
    }
}

dependencies {

    /**
     * Core
     */
    implementation(libs.androidx.core.ktx)

    /**
     * Lifecycle
     */
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    /**
     * Activity
     */
    implementation(libs.androidx.activity.compose)

    /**
     * Compose BOM
     */
    implementation(platform(libs.androidx.compose.bom))

    /**
     * Compose UI
     */
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)

    /**
     * Material
     */
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)

    /**
     * Animation
     */
    implementation(libs.androidx.compose.animation)

    /**
     * Navigation
     */
    implementation(libs.androidx.navigation.compose)

    /**
     * Coil Image Loader
     */
    implementation(libs.coil.compose)

    /**
     * ML Kit
     */
    implementation(libs.mlkit.subject.segmentation)

    /**
     * WebRTC
     */
    implementation(libs.stream.webrtc)

    /**
     * DataStore
     */
    implementation(libs.androidx.datastore.preferences)

    /**
     * Room Database
     */
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)

    ksp(libs.androidx.room.compiler)

    /**
     * Permissions
     */
    implementation(libs.accompanist.permissions)

    /**
     * Ktor Networking
     */
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    /**
     * Security
     */
    implementation(libs.androidx.security.crypto)

    /**
     * Splash Screen
     */
    implementation(libs.androidx.core.splashscreen)

    /**
     * Debug Tools
     */
    debugImplementation(libs.androidx.compose.ui.tooling)
}
