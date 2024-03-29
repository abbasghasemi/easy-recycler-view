buildscript {
    val agp_version by extra("7.2.1")
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0")
    }
}

plugins {
    id("com.android.application") version "7.2.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("com.android.library") version "7.2.1" apply false
    `maven-publish`
}