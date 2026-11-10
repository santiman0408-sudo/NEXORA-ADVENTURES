plugins {
    alias(libs.plugins.android.application)

    // Procesamiento de anotaciones de Room.
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.nexoraadventures"

    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.nexoraadventures"

        // MapLibre Native Android admite API 23.
        minSdk = 23
        targetSdk = 36

        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    // ==========================================
    // ANDROIDX
    // ==========================================

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.constraintlayout)

    // ==========================================
    // MATERIAL DESIGN
    // ==========================================

    implementation(libs.material)

    implementation(
        "androidx.recyclerview:recyclerview:1.4.0"
    )

    implementation(
        "androidx.cardview:cardview:1.0.0"
    )

    implementation(
        "androidx.drawerlayout:drawerlayout:1.2.0"
    )

    // ==========================================
    // ANDROID NAVIGATION
    // ==========================================

    implementation(
        "androidx.navigation:navigation-fragment-ktx:2.9.5"
    )

    implementation(
        "androidx.navigation:navigation-ui-ktx:2.9.5"
    )

    // ==========================================
    // ROOM DATABASE 2.8.5
    // ==========================================

    implementation(
        "androidx.room:room-runtime:2.8.5"
    )

    implementation(
        "androidx.room:room-ktx:2.8.5"
    )

    // Procesador de anotaciones de Room.
    ksp(
        "androidx.room:room-compiler:2.8.5"
    )

    // ==========================================
    // MAPLIBRE NATIVE ANDROID
    // ==========================================

    // Motor cartográfico nativo para Android.
    implementation(
        "org.maplibre.gl:android-sdk:13.7.1"
    )

    // ==========================================
    // UBICACIÓN DEL DISPOSITIVO
    // ==========================================

    // Se conserva para obtener la ubicación GPS.
    // No requiere una clave de Google Maps Platform.
    implementation(
        "com.google.android.gms:play-services-location:21.3.0"
    )

    // ==========================================
    // COROUTINES
    // ==========================================

    implementation(
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2"
    )

    // ==========================================
    // SPLASH SCREEN
    // ==========================================

    implementation(
        "androidx.core:core-splashscreen:1.2.0"
    )

    // ==========================================
    // PRUEBAS
    // ==========================================

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)

    androidTestImplementation(libs.androidx.espresso.core)
}
