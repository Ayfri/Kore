plugins {
	kotlin("jvm") apply false
	alias(libs.plugins.nexus.publish)
}

nexusPublishing {
	repositories {
		sonatype {
			nexusUrl = uri(Project.PUBLISH_URL)
			snapshotRepositoryUrl = uri(Project.SNAPSHOT_PUBLISH_URL)
			username = System.getenv("NEXUS_USERNAME_TOKEN") ?: return@sonatype
			password = System.getenv("NEXUS_PASSWORD_TOKEN") ?: return@sonatype
			packageGroup = Project.GROUP
		}
	}
}

val initializeSonatypeStagingRepository by tasks.existing
subprojects {
	initializeSonatypeStagingRepository {
		shouldRunAfter(tasks.withType<Sign>())
	}
}
