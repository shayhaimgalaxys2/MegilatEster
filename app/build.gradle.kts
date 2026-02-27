import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        load(file.inputStream())
    }
}

android {
    namespace = "com.mobilegiants.megila"
    compileSdk = 36

    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.mobilegiants.megila"
        minSdk = 23
        targetSdk = 36
        versionCode = 29
        versionName = "1.2.4"
        ndk {
            debugSymbolLevel = "FULL"
        }
    }

    signingConfigs {
        create("signingRelease") {
            storeFile = file("keystore/prod/release.keystore")
            storePassword = localProperties.getProperty("signing.storePassword", "")
            keyAlias = "shay"
            keyPassword = localProperties.getProperty("signing.keyPassword", "")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("signingRelease")
        }
    }
}

dependencies {
    // AndroidX
    implementation(libs.appcompat)
    implementation(libs.appcompat.resources)
    implementation(libs.activity.ktx)
    implementation(libs.constraintlayout)
    implementation(libs.cardview)
    implementation(libs.core.ktx)
    implementation(libs.annotation)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.runtime.ktx)

    // Material Design
    implementation(libs.material)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.perf)
    implementation(libs.firebase.config)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)

    // WorkManager (force upgrade for 16KB page size support)
    implementation(libs.work.runtime)

    // Google Ads
    implementation(libs.play.services.ads)

    // Media3
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.ui)

    // Pushwoosh
    implementation(libs.pushwoosh)
    implementation(libs.pushwoosh.firebase)

    // Third-party
    implementation(libs.lottie)
    implementation(libs.sdp)
    implementation(libs.ssp)
    implementation(libs.gson)
}
