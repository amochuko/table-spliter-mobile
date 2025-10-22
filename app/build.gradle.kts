plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.partum.tabsplit"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.ochuko.tabsplit"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("debug") {
            // Dev machine (emulator <-> host)
            manifestPlaceholders["network_security_config"] = "network_security_config_debug"

            // This will generate BuildConfig.API_BASE_URL
            buildConfigField("String", "API_BASE_URL", "\"http://10.0.2.2:4000/\"")
        }

        create("staging") {
            initWith(getByName("debug"))

            // Staging server - test environment
            buildConfigField("String", "API_BASE_URL", "\"http://staging.tabsplit.com/\"")
            manifestPlaceholders["network_security_config"] =
                "network_security_config_release"
        }

        getByName("release") {
            isMinifyEnabled = true
            // Production backend
            buildConfigField("String", "API_BASE_URL", "\"http://api.tabsplit.com/\"")
            manifestPlaceholders["network_security_config"] =
                "network_security_config_release"
        }

        release {
//            isMinifyEnabled = false
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
        buildConfig = true // Used to enable BuildConfig.API_BASE_URL feature
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    implementation(libs.security.crypto)


    // Compose core
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)

    // Material3
    implementation(libs.androidx.compose.material3)

    // Navigation
    implementation(libs.androidx.compose.navigation)

    // ZXing for QR code generation
    implementation(libs.zxing.core)

    // Optional: for preview & debugging
    debugImplementation(libs.androidx.compose.ui.tooling)

}