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
			groupId = "io.github.ayfri"
			artifactId = "datapack-dsl"
			version = "1.0.0"

			pom {
				name = "DataPack DSL"
				description = "A Kotlin library to create Minecraft Datapacks."
				url = "https://example.com/library"

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
					val repositoryUrl = "github.com/Ayfri/Datapack-DSL"

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
			url = uri("https://s01.oss.sonatype.org/service/local/")

			credentials {
				username = System.getenv("NEXUS_USERNAME") ?: return@credentials
				password = System.getenv("NEXUS_PASSWORD") ?: return@credentials
			}
		}
	}
}

signing {
	val key = System.getenv("SIGNING_KEY") ?: return@signing
	val password = System.getenv("SIGNING_PASSWORD") ?: return@signing
	val extension = extensions.getByName("publishing") as PublishingExtension

	useInMemoryPgpKeys(key, password)
	sign(extension.publications)
}
