plugins {
    alias(libs.plugins.android.application)

    id("com.google.gms.google-services")
}

android {
    namespace = "sg.edu.np.mad.pocketchef"
    compileSdk = 34

    defaultConfig {
        applicationId = "sg.edu.np.mad.pocketchef"
        minSdk = 33
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
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-database")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.google.android.material:material:1.7.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.picasso:picasso:2.8")
    implementation("androidx.constraintlayout:constraintlayout:2.0.0")
    implementation("io.github.glailton.expandabletextview:expandabletextview:1.0.4")
    implementation("org.jsoup:jsoup:1.14.3")
    implementation("androidx.core:core-splashscreen:1.0.0-alpha01")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.storage)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    val dialogx_version = "0.0.50.beta2"
    implementation("com.github.kongzue.DialogX:DialogX:${dialogx_version}")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")
    implementation ("com.guolindev.permissionx:permissionx:1.7.1")
}