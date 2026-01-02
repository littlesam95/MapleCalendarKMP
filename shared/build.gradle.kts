import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.implementation
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }
    
    sourceSets {
        commonMain.dependencies {
            // ViewModel
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.androidx.lifecycle.viewmodel.compose)

            // Datetime
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")

            // Ktor
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)

            // Koin
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            // Coroutines
            implementation(libs.kotlinx.coroutines.core)

            // DataStore
            implementation("androidx.datastore:datastore-preferences-core:1.1.1")
            implementation("androidx.datastore:datastore:1.1.1")

            // Firebase Messaging
            implementation("dev.gitlive:firebase-messaging:2.4.0")

            // Napier
            implementation("io.github.aakira:napier:2.7.1")
        }

        androidMain.dependencies {
            // Android 전용 엔진(OkHttp) 및 Koin
            implementation(libs.ktor.client.okhttp)
            implementation(libs.koin.android)

            // Android용 DataStore (Context 관련 기능 때문)
            implementation("androidx.datastore:datastore-preferences:1.1.1")
        }

        iosMain.dependencies {
            // iOS 전용 엔진(Darwin) 및 Koin
            implementation(libs.ktor.client.darwin)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.sixclassguys.maplecalendar.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}