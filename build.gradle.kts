// Central Portal publishing configuration
// Note: Individual modules are configured in publish-conventions.gradle.kts

tasks.register("publishToSonatype") {
	description = "Publishes all modules to Central Portal"
	group = "publishing"

	dependsOn(gradle.includedBuilds.map { it.task(":publishAllPublicationsToCentralRepository") })
	dependsOn(subprojects.map { "${it.path}:publishAllPublicationsToCentralRepository" })
}
