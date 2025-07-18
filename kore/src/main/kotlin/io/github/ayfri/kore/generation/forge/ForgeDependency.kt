package io.github.ayfri.kore.generation.forge

import kotlinx.serialization.Serializable

@Serializable
enum class Ordering {
	AFTER,
	BEFORE,
	NONE,
}

@Serializable
enum class Side {
	CLIENT,
	SERVER,
	BOTH,
}

@Serializable
data class ForgeDependency(
	var modId: String,
	var mandatory: Boolean = true,
	var versionRange: String? = null,
	var ordering: Ordering? = null,
	var side: Side? = null,
)

context(options: ForgeModGenerationOptions)
fun ForgeModProperties.dependency(modId: String, init: ForgeDependency.() -> Unit = {}) {
	val dependencies = (options.dependencies ?: emptyMap()).toMutableMap()
	dependencies[this.modId] = (dependencies[this.modId] ?: emptyList()) + ForgeDependency(modId).apply(init)

	options.dependencies = dependencies
}
