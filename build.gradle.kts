// Central Portal publishing configuration using JReleaser
// Note: Individual modules are configured in publish-conventions.gradle.kts

tasks.register("publishToSonatype") {
	description = "Publishes all modules to Central Portal using JReleaser"
	group = "publishing"

	// Dynamically discover all subprojects that have publishing configured
	val publishingProjects = subprojects.filter { subproject ->
		subproject.plugins.hasPlugin("publish-conventions")
	}

	// Add dependencies for staging repositories
	publishingProjects.forEach { project ->
		dependsOn("${project.path}:publishAllPublicationsToStagingRepository")
	}

	// Add dependencies for JReleaser deployment
	publishingProjects.forEach { project ->
		finalizedBy("${project.path}:jreleaserDeploy")
	}
}
