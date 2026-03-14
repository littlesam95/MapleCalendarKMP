import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    id("com.google.gms.google-services")
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

            // Android용 DataStore (Context 관련 기능 때문)
            implementation("androidx.datastore:datastore-preferences:1.1.1")

            // Android 전용 Firebase BOM
            implementation(project.dependencies.platform("com.google.firebase:firebase-bom:32.7.0"))
            implementation("com.google.firebase:firebase-messaging-ktx")

            // Coil & GIF
            implementation(libs.coil.gif)
        }
        commonMain.dependencies {
            // Shared Component
            implementation(projects.shared)

            // Compose UI
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.materialIconsExtended)

            // Reorderable
            implementation("sh.calvin.reorderable:reorderable:2.4.2")

            // ViewModel
            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.androidx.lifecycle.runtime.compose)

            // Datetime
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")

            // Coil core 및 Compose 지원
            implementation(libs.coil.compose)
            implementation(libs.coil.network.okhttp)

            // Jetpack Compose Navigation
            implementation(libs.androidx.navigation.compose)

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

            // ExoPlayer
            implementation(libs.bundles.media3)

            // Napier
            implementation("io.github.aakira:napier:2.7.1")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.sixclassguys.maplecalendar"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    ndkVersion = "25.1.8937393"

    defaultConfig {
        applicationId = "com.sixclassguys.maplecalendar"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 18
        versionName = "0.4.2"

        buildConfigField("int", "VERSION_CODE", "${versionCode}")
        buildConfigField("String", "VERSION_NAME", "\"${versionName}\"")

        ndk {
            // 구글 플레이에서 요구하는 64비트 아키텍처를 포함합니다.
            abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64"))
        }
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildFeatures {
        buildConfig = true // 이미 true로 되어있을 겁니다.
    }
    buildTypes {
        getByName("release") {
            // 1. 난독화 및 최적화 활성화 (앱 용량 줄이기 위해 권장)
            isMinifyEnabled = true

            // 2. 기본 프로가드 설정 적용
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            // 3. 🚀 네이티브 코드 디버그 기호 포함 (두 번째 경고 해결)
            ndk {
                debugSymbolLevel = "FULL"
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

