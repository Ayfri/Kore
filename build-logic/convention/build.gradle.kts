plugins {
	`kotlin-dsl`
}

dependencies {
	implementation(libs.gradle.plugin.kotlin)
	implementation(libs.gradle.plugin.ksp)
	implementation(libs.gradle.plugin.vanniktech.publish)
}

kotlin {
	jvmToolchain(25)
}