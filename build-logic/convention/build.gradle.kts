plugins {
	`kotlin-dsl`
}

dependencies {
	implementation(libs.gradle.plugin.kotlin)
	implementation(libs.gradle.plugin.vanniktech.publish)
}

kotlin {
	jvmToolchain(21)
}