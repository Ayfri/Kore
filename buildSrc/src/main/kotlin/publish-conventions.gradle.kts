plugins {
	`maven-publish`
	signing
}

repositories {
	mavenCentral()
}

val kotlinSourcesJar: Task by tasks.getting
val publicationName: String? by project.ext
val publicationDescription: String? by project.ext

publishing {
	publications {
		create<MavenPublication>("kotlin") {
			from(components["kotlin"])

			artifact(kotlinSourcesJar)

			groupId = Project.GROUP
			version = "${Project.VERSION}-${mainProjectProperty("minecraft.version")}"

			pom {
				name = publicationName
				description = publicationDescription
				url = Project.URL

				packaging = "jar"

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
			url = when {
				project.version.toString().endsWith("SNAPSHOT") -> uri(Project.SNAPSHOT_PUBLISH_URL)
				else -> uri(Project.PUBLISH_URL)
			}

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
