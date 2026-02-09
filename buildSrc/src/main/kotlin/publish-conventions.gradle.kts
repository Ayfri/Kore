plugins {
	kotlin("jvm")
	id("com.vanniktech.maven.publish")
}

version = "${Project.VERSION}-${mainProjectProperty("minecraft.version")}"
group = Project.GROUP

mavenPublishing {
	publishToMavenCentral(automaticRelease = true)

	if (System.getenv("CI") != null) {
		signAllPublications()
	}

	coordinates(Project.GROUP, project.name, version.toString())

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
