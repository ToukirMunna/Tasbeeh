import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.services)
}

val localPropsProvider = providers.fileContents(
    rootProject.layout.projectDirectory.file("local.properties")
).asText.map { content ->
    Properties().apply { load(content.reader()) }
}

android {
    namespace = "com.toukir.tasbeeh"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.toukir.tasbeeh.pro"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "4.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            val keystoreFile = rootProject.layout.projectDirectory.file("release-key-TKR.jks").asFile
            if (keystoreFile.exists()) {
                val localProps = localPropsProvider.orNull
                storeFile = keystoreFile
                storePassword = localProps?.getProperty("RELEASE_STORE_PASSWORD").orEmpty()
                keyAlias = localProps?.getProperty("RELEASE_KEY_ALIAS").orEmpty()
                keyPassword = localProps?.getProperty("RELEASE_KEY_PASSWORD").orEmpty()
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
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
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.appcompat)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    
    // DataStore Preferences
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    // Gson
    implementation("com.google.code.gson:gson:2.10.1")
    // Coil
    implementation("io.coil-kt:coil-compose:2.7.0")

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.play.services.auth)

    // Lifecycle
    implementation(libs.androidx.lifecycle.process)
}
