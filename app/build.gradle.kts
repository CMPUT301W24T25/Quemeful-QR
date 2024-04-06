plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.android.quemeful_qr"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.android.quemeful_qr"
        minSdk = 34
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"


    }


    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")


            buildTypes {
                getByName("release") {
                    isMinifyEnabled = false
                    proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
                }
            }

            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_1_8
                targetCompatibility = JavaVersion.VERSION_1_8
            }


        }

        dependencies {

//            updated to a general file path for Javadocs.
            //compileOnly(files("${android.sdkDirectory}/platforms/${android.compileSdkVersion}/android.jar"))
            implementation("com.squareup.okhttp3:okhttp:4.10.0") //
            implementation("org.apache.directory.studio:org.apache.commons.io:2.4") //
            implementation("com.google.firebase:firebase-messaging:23.4.1") // firebase
            implementation("com.google.firebase:firebase-storage:20.3.0") // firebase
            implementation ("com.google.android.material:material:<version>")
            implementation("com.squareup.okhttp3:okhttp:4.10.0")
            implementation("org.apache.directory.studio:org.apache.commons.io:2.4")
            implementation("com.google.firebase:firebase-messaging:23.4.1")
            implementation("com.google.firebase:firebase-storage:20.3.0")

            implementation("androidx.appcompat:appcompat:1.6.1")
            implementation("com.google.android.material:material:1.11.0")
            implementation("androidx.constraintlayout:constraintlayout:2.1.4")
            implementation("androidx.compose.ui:ui-desktop:1.6.2") //
            testImplementation("junit:junit:4.13.2")
            implementation("de.hdodenhof:circleimageview:3.1.0") //
            androidTestImplementation("androidx.test.ext:junit:1.1.5")
            androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
            implementation("com.etebarian:meow-bottom-navigation-java:1.2.0") // customized app bottom navigation bar
            implementation("androidx.navigation:navigation-fragment:2.7.6")
            implementation("com.google.firebase:firebase-firestore:24.10.3") // firebase
            implementation("com.github.bumptech.glide:glide:4.13.0") //
            implementation("com.github.bumptech.glide:compiler:4.13.0") //
            implementation("com.caverock:androidsvg:1.4")
            implementation("com.google.zxing:core:3.4.1") //scan QR
            implementation("com.journeyapps:zxing-android-embedded:4.2.0") // generate QR
            coreLibraryDesugaring ("com.android.tools:desugar_jdk_libs:2.0.2")
            implementation("androidx.multidex:multidex:2.0.1")
            testImplementation("org.junit.jupiter:junit-jupiter-api:5.0.1")
            testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.0.1")
            implementation ("org.osmdroid:osmdroid-wms:6.1.1") //
            testImplementation ("junit:junit:4.13.2")
            testImplementation ("androidx.test:core:1.3.0")
            testImplementation ("androidx.test.ext:junit:1.1.2")
            testImplementation("org.mockito:mockito-core:3.3.3")
            testImplementation("androidx.fragment:fragment-testing:1.3.6")
            androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")

        }
    }
    buildFeatures {
        viewBinding = true
    }
}
dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.test.espresso:espresso-intents:3.5.1")
    implementation ("androidx.preference:preference:1.2.1")
    implementation("com.google.firebase:firebase-inappmessaging:20.4.0") //

    implementation ("com.google.firebase:firebase-analytics:21.5.1")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("com.google.firebase:firebase-database:20.3.1") //


    implementation("androidx.activity:activity:1.8.0") //
}
dependencies {
    implementation("com.google.firebase:firebase-auth:22.3.1")
}


