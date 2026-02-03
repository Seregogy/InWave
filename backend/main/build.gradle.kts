plugins {
	alias(libs.plugins.jetbrains.kotlin.jvm)
}

group = "com.inwave"
version = "0.0.1"

tasks.test {
	useJUnitPlatform()
}

kotlin {
	jvmToolchain(21)
}

dependencies {
    implementation(project(":domain"))
	testImplementation(kotlin("test"))
}