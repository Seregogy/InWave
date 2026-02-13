plugins {
	alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.ktor)
    application

    kotlin("plugin.serialization") version "2.1.21"
}

group = "com.inwave"
version = "0.0.1"

application {
    mainClass.set("com.inwave.backend.MainKt")
}

tasks.test {
	useJUnitPlatform()
}

kotlin {
	jvmToolchain(21)
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":backend:data"))
	testImplementation(kotlin("test"))
    testImplementation(libs.ktor.server.test.host)

    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.di)
    implementation(libs.ktor.server.content.negotation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.config.yaml)

    implementation(libs.koin.ktor)

    implementation(libs.logback.classic)
}