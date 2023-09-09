plugins {
	`maven-publish`
	signing
}

repositories {
	mavenCentral()
}

publishing {
	publications {
		create<MavenPublication>("kotlin") {
			from(components["kotlin"])

			groupId = Project.GROUP
			artifactId = "${Project.NAME.lowercase()}-${project.name}"
			version = Project.VERSION

			pom {
				name = project.name
				description = Project.DESCRIPTION
				url = Project.URL

				licenses {
					license {
						name = "GPLv3"
						url = "https://www.gnu.org/licenses/gpl-3.0.en.html"
					}
				}

				developers {
					developer {
						id = "Ayfri"
						name = "Roy Pierre"
						email = "pierre.ayfri@gmail.com"
					}
				}

				scm {
					val repositoryUrl = Project.URL

					connection = "scm:git:git://$repositoryUrl.git"
					developerConnection = "scm:git:ssh://$repositoryUrl.git"
					url = "https://$repositoryUrl"
				}
			}
		}
	}

	repositories {
		maven {
			name = "nexus"
			url = uri(Project.PUBLISH_URL)

			credentials {
				username = System.getenv("NEXUS_USERNAME") ?: return@credentials
				password = System.getenv("NEXUS_PASSWORD") ?: return@credentials
			}
		}
	}
}

signing {
	sign(publishing.publications["kotlin"])
}
