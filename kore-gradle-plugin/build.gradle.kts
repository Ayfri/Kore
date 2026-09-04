import java.time.Duration
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	`java-gradle-plugin`
	kotlin("jvm")
	alias(libs.plugins.gradle.plugin.publish)
	id("kotest-conventions")
	id("publish-conventions")
}

repositories {
	mavenCentral()
}

kotlin {
	jvmToolchain(25)

	// Consumers load this plugin into their own Gradle daemon, which is commonly still on Java 17 or 21.
	compilerOptions {
		jvmTarget = JvmTarget.JVM_17
		freeCompilerArgs.add("-Xjdk-release=17")
	}
}

java {
	targetCompatibility = JavaVersion.VERSION_17
	sourceCompatibility = JavaVersion.VERSION_17
}

gradlePlugin {
	website = Project.WEBSITE_URL
	vcsUrl = "https://${Project.GITHUB_URL}"

	plugins.create("kore") {
		id = "io.github.ayfri.kore"
		implementationClass = "io.github.ayfri.kore.gradle.KorePlugin"
		displayName = "Kore"
		description = "Builds a Kore datapack, links it into Minecraft worlds and reloads a running server."
		tags = listOf("minecraft", "datapack", "kore", "kotlin")
	}

	// Makes the plugin under development available to TestKit builds through `withPluginClasspath()`.
	testSourceSets(sourceSets["test"])
}

dependencies {
	testImplementation(gradleTestKit())
}

// TestKit spawns a nested Gradle build per test, so the default 2 minutes is not always enough on a cold daemon.
tasks.test {
	timeout = Duration.ofMinutes(15)
}
