plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.luxevistaapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.luxevistaapp"
        minSdk = 31
        targetSdk = 34
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

    packaging {
        resources {
            excludes += "META-INF/NOTICE.md"
            excludes += "META-INF/LICENSE.md"
        }


        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }


    }

    dependencies {

        implementation(libs.appcompat)
        implementation(libs.material)
        implementation(libs.activity)
        implementation(libs.constraintlayout)
        implementation(libs.firebase.auth)
        implementation(libs.firebase.database)
        implementation(libs.recyclerview)
        implementation(libs.firebase.firestore)
        testImplementation(libs.junit)
        androidTestImplementation(libs.ext.junit)
        androidTestImplementation(libs.espresso.core)
        implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
        implementation(platform("com.squareup.picasso:picasso:2.71828"))

        implementation("com.github.bumptech.glide:glide:4.16.0")

        implementation(libs.android.mail)
        implementation(libs.android.activation)

        implementation ("com.github.bumptech.glide:glide:4.15.1")
        annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")

    }
}