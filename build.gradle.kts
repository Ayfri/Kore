plugins {
	kotlin("jvm") apply false
	alias(libs.plugins.nexus.publish)
}

nexusPublishing {
	repositories {
		sonatype {
			nexusUrl = uri(Project.PUBLISH_URL)
			snapshotRepositoryUrl = uri(Project.SNAPSHOT_PUBLISH_URL)
			username = System.getenv("NEXUS_USER") ?: return@sonatype
			password = System.getenv("NEXUS_PASSWORD") ?: return@sonatype
		}
	}
}

val initializeSonatypeStagingRepository by tasks.existing
subprojects {
	initializeSonatypeStagingRepository {
		shouldRunAfter(tasks.withType<Sign>())
	}
}
