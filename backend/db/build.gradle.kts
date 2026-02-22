plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)

    kotlin("plugin.serialization") version "2.1.21"
}
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}

dependencies {
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.kotlin.datetime)
    implementation(libs.exposed.json)
    implementation(libs.exposed.driver.postgres)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.h2)
}