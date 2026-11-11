plugins {
	`kotlin-dsl`
}

dependencies {
	/** Compile-only, only the website's script loads Kobweb. Its runtime variant holds the commonmark and front matter types the generator uses. */
	compileOnly(libs.gradle.plugin.kobwebx.markdown) {
		attributes {
			attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
		}
	}
	implementation(libs.gradle.plugin.kotlin)
	implementation(libs.gradle.plugin.ksp)
	implementation(libs.gradle.plugin.vanniktech.publish)
}

kotlin {
	jvmToolchain(25)
}