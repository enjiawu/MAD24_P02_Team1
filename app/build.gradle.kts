plugins {
    alias(libs.plugins.android.application)

    id("com.google.gms.google-services")
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "sg.edu.np.mad.pocketchef"
    compileSdk = 34

    defaultConfig {
        applicationId = "sg.edu.np.mad.pocketchef"
        minSdk = 24
        targetSdk = 34
        versionCode = 8
        versionName = "1.42"

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

    buildFeatures {
        viewBinding = true
        mlModelBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-dynamic-links")
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-appcheck:16.0.0")
    implementation("com.google.firebase:firebase-appcheck-safetynet:16.0.0")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.google.android.material:material:1.7.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.picasso:picasso:2.8")
    implementation("io.github.glailton.expandabletextview:expandabletextview:1.0.4")
    implementation("org.jsoup:jsoup:1.14.3")
    implementation("androidx.core:core-splashscreen:1.0.0-alpha01")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.mlkit:image-labeling:17.0.8")
    implementation("org.tensorflow:tensorflow-lite:2.14.0")
    implementation ("org.tensorflow:tensorflow-lite-gpu:2.8.0")
    implementation ("org.tensorflow:tensorflow-lite-select-tf-ops:2.8.0")
    implementation ("org.tensorflow:tensorflow-lite-support:0.3.1")
    implementation ("org.tensorflow:tensorflow-lite-metadata:0.3.0")
    implementation ("org.tensorflow:tensorflow-lite-task-text:0.2.0")
    implementation ("com.google.guava:guava:31.0.1-android")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
    val dialogx_version = "0.0.50.beta2"
    implementation("com.github.kongzue.DialogX:DialogX:${dialogx_version}")
    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")
    implementation ("com.guolindev.permissionx:permissionx:1.7.1")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.android.gms:play-services-maps:18.0.0")
    implementation("com.google.android.libraries.places:places:3.5.0")
    implementation("com.google.maps.android:android-maps-utils:2.2.0")
    implementation ("com.google.code.gson:gson:2.8.9")
    implementation ("com.orhanobut:logger:2.2.0")
    implementation ("com.google.zxing:core:3.4.1")
    implementation ("com.journeyapps:zxing-android-embedded:4.2.0")
    implementation ("com.squareup.okhttp3:okhttp:4.9.1")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.7.2")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.storage)
    implementation(libs.tensorflow.lite.support)
    implementation(libs.tensorflow.lite.metadata)
    implementation(libs.core.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}