plugins {
	kotlin("jvm")
	`maven-publish`
	id("org.jreleaser")
}

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
						connection = "scm:git:https://${Project.URL}.git"
						developerConnection = "scm:git:https://${Project.URL}.git"
						url = "https://${Project.URL}"
					}
				}
			}
		}

		repositories {
			maven {
				name = Project.STAGING_REPO_NAME
				url = uri(layout.buildDirectory.dir("staging-deploy"))
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
		mode = org.jreleaser.model.Signing.Mode.MEMORY

		memory {
			keys = providers.environmentVariable("GPG_KEY").orNull
			passphrase = providers.environmentVariable("GPG_PASSWORD").orNull
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
					stagingRepository(layout.buildDirectory.dir("staging-deploy").get().asFile.absolutePath)
				}
			}
		}
	}

	release {
		github {
			enabled = false
		}
	}
}
