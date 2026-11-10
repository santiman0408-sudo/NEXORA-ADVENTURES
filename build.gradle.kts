plugins {
    alias(libs.plugins.android.application) apply false

    // Procesador de código para Room Database.
    id("com.google.devtools.ksp") version "2.3.10" apply false
}

