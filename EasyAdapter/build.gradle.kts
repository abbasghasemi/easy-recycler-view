plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
}

android {
    namespace = "com.ag.easy.recyclerview"
    compileSdk = 33

    defaultConfig {
        minSdk = 21
        aarMetadata {
            minCompileSdk = 21
        }
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
//            groupId =  "com.github.abbasghasemi"
                artifactId = "easy-recyclerview-adapter"
                version = "1.1.4"

//            pom {
//                name.set("Easy RecyclerView Adapter")
//                description.set("Implement RecyclerView Adapter with minimum complexity.")
//                url.set("https://github.com/abbasghasemi/easy-recyclerview-adapter")
//                licenses {
//                    license {
//                        name.set("The Apache License, Version 2.0")
//                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
//                    }
//                }
//                developers {
//                    developer {
//                        id.set("abbasghasemi")
//                        name.set("Abbas Ghasemi")
//                    }
//                }
//            }
            }
        }

        repositories {
            maven {
                url = uri("https://jitpack.io")
            }
        }
    }
}

tasks.withType<AbstractArchiveTask> {
    if(this.name == "releaseSourcesJar" || this.name == "debugSourcesJar") {
        enabled = false
    }
}