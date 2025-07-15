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

		repositories {
			maven {
				name = "staging"
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
		verify = false
		// Use default signing mode (MEMORY) which is more reliable in CI.
		// It requires JRELEASER_GPG_SECRET_KEY and JRELEASER_GPG_PUBLIC_KEY env variables.
		// JReleaser will automatically look for the JRELEASER_GPG_PASSPHRASE environment variable.
	}

	deploy {
		maven {
			mavenCentral {
				create("sonatype") {
					active = org.jreleaser.model.Active.ALWAYS
					url = Project.CENTRAL_PORTAL_URL
					username = providers.environmentVariable("CENTRAL_USERNAME").orNull
					password = providers.environmentVariable("CENTRAL_PASSWORD").orNull
					stagingRepository("build/staging-deploy")
				}
			}
		}
	}
}

// Make sure JReleaser deploy tasks depend on publishing tasks
tasks.named("jreleaserDeploy") {
	dependsOn("publishAllPublicationsToStagingRepository")
}

fun mainProjectProperty(name: String): String {
	return project.rootProject.property(name).toString()
}
