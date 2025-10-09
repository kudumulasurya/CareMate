import org.gradle.kotlin.dsl.annotationProcessor
import org.gradle.kotlin.dsl.implementation

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
}

android {
    namespace = "com.example.medicare"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.medicare"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    // Firebase BOM (Manages versions automatically)
    implementation(platform("com.google.firebase:firebase-bom:32.7.0")) // Use latest BOM

    // Firebase Auth & Firestore (No need to specify versions separately)
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")

    // Firebase Features
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-database")

    // Google Play Services Auth
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // AndroidX & UI Libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Other Dependencies
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation(libs.converter.gson)
    implementation(libs.gson)
    implementation(libs.glide)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation ("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.firebase:firebase-storage:20.1.0")
    implementation("com.cloudinary:cloudinary-android:2.4.0")
    //noinspection NewerVersionAvailable
    implementation("com.cloudinary:cloudinary-android:3.0.2")



}
