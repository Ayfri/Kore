pluginManagement {
	repositories {
		gradlePluginPortal()
		mavenCentral()
	}
}

dependencyResolutionManagement {
	repositories {
		mavenCentral()
		gradlePluginPortal()
	}

	versionCatalogs {
		create("libs") {
			from(files("../gradle/libs.versions.toml"))
		}
	}
}

rootProject.name = "build-logic"

include(":convention")