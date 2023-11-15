
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs")
}

android {
    namespace = "com.mapsted.compose_demo"

    defaultConfig {
        applicationId = "com.mapsted.compose_demo"
        minSdk = 24
        compileSdk = 33
        versionCode = 5
        versionName = "1.0.4"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug {
            multiDexEnabled = true
            isDebuggable = true
            isJniDebuggable = true
            versionNameSuffix = "-debug"
        }
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
        compose = true
        dataBinding = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.8"
    }
    packaging {
        resources.excludes.add("META-INF/gradle/*")
        resources.excludes.add("META-INF/*")
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.compose.material:material:1.4.3")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.core:core-splashscreen:1.0.0")
    val navVersion = "2.6.0"
    implementation("androidx.navigation:navigation-compose:$navVersion")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation("com.google.android.gms:play-services-base:18.0.1")
    implementation("com.google.android.gms:play-services-tasks:18.0.1")

    // Mapsted
    val mapstedSdkVersion = "5.4.0-rc-1"
    implementation("com.mapsted:sdk-map-ui:$mapstedSdkVersion")
    implementation("com.mapsted:sdk-map:$mapstedSdkVersion")
    implementation("com.mapsted:sdk-core:$mapstedSdkVersion")
}
