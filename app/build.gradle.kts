plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "PTC.quickly"
    compileSdk = 34

    defaultConfig {
        applicationId = "PTC.quickly.equipo"
        minSdk = 24
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }

    packaging {
        resources {
            excludes += "META-INF/versions/9/OSGI-INF/MANIFEST.MF"
        }
    }

}

dependencies {
    implementation ("com.itextpdf:itextg:5.5.10")
    implementation("com.airbnb.android:lottie:6.0.0")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
<<<<<<< HEAD
    implementation(libs.material) // Mantén solo una versión
    implementation("com.oracle.database.jdbc:ojdbc6:11.2.0.4") // Elimina el duplicado
=======
    implementation(libs.material)
>>>>>>> master
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.activity)
<<<<<<< HEAD
    implementation(files("libs/mail.jar"))
    implementation(files("libs/additionnal.jar"))
    implementation(libs.firebase.common.ktx)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.identity.jvm)
    implementation(libs.androidx.ui.text.android)

=======
    implementation(files("libs\\mail.jar"))
    implementation(files("libs\\additionnal.jar"))
    implementation(files("libs\\activation.jar"))
    implementation(libs.firebase.common.ktx)
    implementation(libs.firebase.storage.ktx)
>>>>>>> master
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
