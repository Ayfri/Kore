plugins {
	kotlin("jvm")
	`maven-publish`
	signing
}

val isKore get() = project.name == "kore"

val javadocJar = task("javadocJar", Jar::class) {
	if (isKore) dependsOn(":generation:run")
	archiveClassifier.set("javadoc")
	from(tasks.javadoc)
}

val sourceJar = task("sourceJar", Jar::class) {
	dependsOn(tasks["classes"])
	if (isKore) dependsOn(":generation:run")
	archiveClassifier.set("sources")
	from(sourceSets["main"].allSource)
}

afterEvaluate {
	publishing {
		publications {
			create<MavenPublication>("maven") {
				from(components["java"])

				artifact(sourceJar)
				artifact(javadocJar)

				groupId = Project.GROUP
				version = "${Project.VERSION}-${mainProjectProperty("minecraft.version")}"

				pom {
					val publicationName: String? by project.ext
					val publicationDescription: String? by project.ext

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

					issueManagement {
						system = "GitHub"
						url = "${Project.URL}/issues"
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
		val signingKey: String? by project ?: return@signing
		val signingPassword: String? by project ?: return@signing

		useInMemoryPgpKeys(signingKey, signingPassword)
		sign(publishing.publications["maven"])
	}
}
