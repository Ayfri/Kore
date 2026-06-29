plugins {
	kotlin("jvm")
	id("com.vanniktech.maven.publish")
}

val minecraftVersion = providers.gradleProperty("minecraft.version")
val isSnapshotBuild = providers.gradleProperty("kore.publish.snapshot")
	.map(String::toBoolean)
	.orElse(false)


val publicationVersion = minecraftVersion.zip(isSnapshotBuild) { mcVersion, snapshot ->
	buildString {
		append(Project.VERSION)
		append("-")
		append(mcVersion)
		if (snapshot) append("-SNAPSHOT")
	}
}

group = Project.GROUP
version = publicationVersion.get()

mavenPublishing {
	publishToMavenCentral(automaticRelease = !isSnapshotBuild.get())

	if (providers.environmentVariable("CI").isPresent && !isSnapshotBuild.get()) {
		signAllPublications()
	}

	coordinates(Project.GROUP, project.name, publicationVersion.get())

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
