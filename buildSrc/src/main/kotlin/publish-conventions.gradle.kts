plugins {
	kotlin("jvm")
	`maven-publish`
	id("org.jreleaser")
}

// Set project version for JReleaser
project.version = "${Project.VERSION}-${mainProjectProperty("minecraft.version")}"

val javadocJar = tasks.register("javadocJar", Jar::class, fun Jar.() {
	archiveClassifier = "javadoc"
	from(tasks.javadoc)
})

val sourceJar = tasks.register("sourceJar", Jar::class, fun Jar.() {
	dependsOn(tasks["classes"])
	archiveClassifier = "sources"
	from(sourceSets["main"].allSource)
})

afterEvaluate {
	publishing {
		publications {
			create<MavenPublication>(Project.MAVEN_PUBLICATION_NAME) {
				from(components["java"])

				artifact(sourceJar)
				artifact(javadocJar)

				groupId = Project.GROUP
				version = "${Project.VERSION}-${mainProjectProperty("minecraft.version")}"

				pom {
					name = project.name
					description = Project.PROJECT_DESCRIPTION
					url = "https://${Project.URL}"

					licenses {
						license {
							name = Project.LICENSE_NAME
							url = Project.LICENSE_URL
						}
					}

					developers {
						developer {
							id = Project.DEVELOPER_ID
							name = Project.DEVELOPER_NAME
							email = Project.DEVELOPER_EMAIL
						}
					}

					scm {
						connection = "scm:git:git://github.com/${Project.URL}.git"
						developerConnection = "scm:git:ssh://github.com:${Project.URL}.git"
						url = "https://${Project.URL}/tree/master"
					}
				}
			}
		}
	}
}

jreleaser {
	project {
		copyright = "${Project.COPYRIGHT_YEAR} ${Project.DEVELOPER_NAME}"
		description = Project.PROJECT_DESCRIPTION
	}

	signing {
		active = org.jreleaser.model.Active.ALWAYS
		armored = true
		verify = false
		mode = org.jreleaser.model.Signing.Mode.COMMAND

		command {
			keyName = providers.environmentVariable("GPG_KEY_ID").orNull
			passphrase = providers.environmentVariable("JRELEASER_GPG_PASSPHRASE").orElse(providers.environmentVariable("GPG_PASSWORD")).orNull
		}
	}

	deploy {
		maven {
			mavenCentral {
				create("sonatype") {
					active = org.jreleaser.model.Active.ALWAYS
					url = Project.CENTRAL_PORTAL_URL
					username = providers.environmentVariable("CENTRAL_USERNAME").orNull
					password = providers.environmentVariable("CENTRAL_PASSWORD").orNull
					stagingRepository(Project.STAGING_REPO_NAME)
				}
			}
		}
	}
}

fun mainProjectProperty(name: String): String {
	return project.rootProject.property(name).toString()
}
