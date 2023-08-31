buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.0.0")
        classpath("org.jfrog.buildinfo:build-info-extractor-gradle:4.24.23")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.6.0")
    }
}
plugins {
    id("com.android.application") version "8.1.0" apply false
    id("org.jetbrains.kotlin.android") version "1.8.22" apply false
}