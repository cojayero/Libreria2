plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    //id("com.google.devtools.ksp")
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.libreria"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.libreria"
        minSdk = 34
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
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.room.compiler) {exclude(group = "com.intellij", module = "annotations")     }
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)
    // Retrofit
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.material)
    implementation("androidx.compose.compiler:compiler:1.5.15")
    implementation("androidx.compose.ui:ui:1.7.5")
    implementation("androidx.compose.foundation:foundation:1.7.5")
    // Si usas Material 3
    implementation("androidx.compose.material3:material3:1.3.1")
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.material)
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.extensions)
    implementation(libs.camera.lifecycle)
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.barcode.scanning)
    ksp(libs.hilt.compiler)
    implementation("androidx.activity:activity-compose:1.8.2")
    // Importar el BOM de Compose
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    // Dependencias de Compose (sin especificar versiones, ya que el BOM las gestiona)
    implementation("androidx.compose.material:material")
    implementation("androidx.compose.material3:material3") // Si usas Material 3
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.foundation:foundation")
    // Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)



    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
configurations.all {
    resolutionStrategy {
        force( "androidx.core:core-ktx:1.6.0")
    }
}