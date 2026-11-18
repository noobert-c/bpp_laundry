plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.bpp_laundry"
    compileSdk {
        version = release(37)
    }
    buildFeatures {
        compose = true
    }
    defaultConfig {
        applicationId = "com.example.bpp_laundry"
        minSdk = 28
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // Compose BOM: menyelaraskan versi library Compose
    implementation(platform("androidx.compose:compose-bom:2025.01.00"))

    // Android dependencies
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.foundation.layout)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.tv.material)
    implementation(libs.androidx.ui.text)
    implementation(libs.androidx.ui.unit)
    implementation(libs.material)

    // Jetpack Compose
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.compose.runtime:runtime")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.material:material-ripple")

    // Debug
    debugImplementation("androidx.compose.ui:ui-tooling")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    implementation("androidx.navigation:navigation-compose:2.9.6")
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.datastore.preferences)
    implementation(libs.lifecycle.viewmodel.compose)

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
}