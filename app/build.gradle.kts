plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.cinebook"
    compileSdk = 35

    // Add this block for view binding
    buildFeatures {
        dataBinding = true
        viewBinding = true
    }

    defaultConfig {
        applicationId = "com.example.cinebook"
        minSdk = 24
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
}
dependencies {
    // AndroidX core libs
    implementation(libs.appcompat)
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.cardview:cardview:1.0.0")
    // Material design
    implementation(libs.material) // or hardcode the latest version like below:
    // implementation("com.google.android.material:material:1.9.0")
    // Lifecycle (ViewModel + LiveData with Kotlin extensions)
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.6")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.6")
    // Glide (image loading)
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation ("com.google.android.material:material:1.9.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
    // Retrofit + Gson
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    // ZXing (barcode/QR scanning)
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation ("com.google.code.gson:gson:2.10.1")
    // iText PDF
    implementation("com.itextpdf:itext7-core:7.2.5")
    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
