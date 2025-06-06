plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.plantnetapp"
    compileSdk = 35

    defaultConfig {
        applicationId           = "com.example.plantnetapp"
        minSdk                  = 24
        targetSdk               = 35
        versionCode             = 1
        versionName             = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        // → Aligner Java sur 1.8
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        // → Émettre du bytecode Java 1.8
        jvmTarget = "1.8"
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
}

dependencies {
    // Core & UI
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation(libs.gson)

    // Unit tests
    testImplementation(libs.junit)
    testImplementation("org.mockito:mockito-core:4.5.1")

    // Instrumented tests
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    // CameraX
    val cameraxVersion = "1.2.0"
    implementation("androidx.camera:camera-core:$cameraxVersion")
    implementation("androidx.camera:camera-camera2:$cameraxVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
    implementation("androidx.camera:camera-view:1.2.0")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.jackson.databind)
    implementation (libs.gson)
    testImplementation(libs.junit)
    testImplementation(libs.androidx.monitor)
    testImplementation(libs.androidx.core)
    testImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation (libs.androidx.core.v150)
    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.androidx.junit.v115)
    androidTestImplementation(libs.androidx.rules)
}
