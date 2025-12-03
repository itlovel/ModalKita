import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    kotlin("plugin.serialization") version "2.2.20"
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
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

            // Engine Ktor untuk Android
            implementation("io.ktor:ktor-client-okhttp:3.3.3")
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(projects.shared)
            implementation(compose.materialIconsExtended)

            // ===== Supabase (pilih salah satu gaya: BOM ATAU versi eksplisit) =====

            // Gaya 1: pakai BOM (rekomendasi, biar versi konsisten)
            implementation(project.dependencies.platform("io.github.jan-tennert.supabase:bom:3.2.6"))
            implementation("io.github.jan-tennert.supabase:auth-kt")
            implementation("io.github.jan-tennert.supabase:postgrest-kt")
            implementation("io.github.jan-tennert.supabase:storage-kt")

            // ===== Ktor client core (wajib satu versi yang sama) =====
            implementation("io.ktor:ktor-client-core:3.3.3")
            implementation("io.ktor:ktor-client-cio:3.3.3")

            // ===== Coroutines =====
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.7.1")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.example.modalkita"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.example.modalkita"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
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

