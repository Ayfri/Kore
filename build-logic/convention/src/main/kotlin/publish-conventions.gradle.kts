plugins {
	id("com.vanniktech.maven.publish")
}

val minecraftVersion = providers.gradleProperty("minecraft.version")
val isSnapshotBuild = providers.gradleProperty("kore.publish.snapshot")
	.map(String::toBoolean)
	.orElse(false)
	.get()

// Every published module shares one version, `<koreVersion>-<mcVersion>`, e.g. `2.13.1-26.2`. The Gradle plugin has no
// use for the Minecraft suffix, but carrying it keeps every release uniform and leaves consumers a single string.
val publicationVersion = buildString {
	append(Project.VERSION)
	append("-")
	append(minecraftVersion.get())
	if (isSnapshotBuild) append("-SNAPSHOT")
}

group = Project.GROUP
version = publicationVersion

mavenPublishing {
	publishToMavenCentral(automaticRelease = !isSnapshotBuild)

	if (providers.environmentVariable("CI").isPresent && !isSnapshotBuild) {
		signAllPublications()
	}

	coordinates(Project.GROUP, project.name, publicationVersion)

	pom {
		name = project.name
		description = Project.DESCRIPTION
		url = Project.WEBSITE_URL

		inceptionYear = Project.COPYRIGHT_YEAR

		issueManagement {
			system = "GitHub"
			url = "https://${Project.GITHUB_URL}/issues"
		}

		licenses {
			license {
				name = Project.LICENSE
				url = Project.LICENSE_URL
				distribution = "repo"
			}
		}

		developers {
			developer {
				id = Project.DEVELOPER_ID
				name = Project.DEVELOPER_NAME
				email = Project.DEVELOPER_EMAIL
				url = Project.DEVELOPER_URL
			}
		}

		scm {
			connection = "scm:git:git://${Project.GITHUB_URL}.git"
			developerConnection = "scm:git:ssh://git@${Project.GITHUB_URL}.git"
			url = "https://${Project.GITHUB_URL}"
		}
	}
}
