plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.fedradio"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.fedradio"
        minSdk = 29              // Android 10+ per the FED-Radio spec
        targetSdk = 34
        versionCode = 4
        versionName = "2.1.0"
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

    // CRITICAL: audio files must stay UNCOMPRESSED in the APK so that
    // MediaPlayer can open them via AssetFileDescriptor (openFd).
    androidResources {
        noCompress.addAll(listOf("wav", "mp3", "m4a", "ogg", "flac"))
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // First-party AndroidX only — no third-party SDKs, no network libraries.
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.media:media:1.7.0")   // MediaBrowserServiceCompat (Android Auto bridge)

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
