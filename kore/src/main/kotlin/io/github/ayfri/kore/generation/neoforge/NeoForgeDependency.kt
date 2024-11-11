package io.github.ayfri.kore.generation.neoforge

import io.github.ayfri.kore.generation.forge.Ordering
import io.github.ayfri.kore.generation.forge.Side
import kotlinx.serialization.Serializable

@Serializable
enum class NeoForgeDependencyType {
	REQUIRED,
	OPTIONAL,
	INCOMPATIBLE,
	DISCOURAGED,
}

@Serializable
data class NeoForgeDependency(
	var modId: String,
	var ordering: Ordering? = null,
	var referralUrl: String? = null,
	var side: Side? = null,
	var reason: String? = null,
	var type: NeoForgeDependencyType? = null,
	var versionRange: String? = null,
)

context(NeoForgeModGenerationOptions)
fun NeoForgeModProperties.dependency(modId: String, init: NeoForgeDependency.() -> Unit = {}) {
	val dependencies = (dependencies ?: emptyMap()).toMutableMap()
	dependencies[this.modId] = (dependencies[this.modId] ?: emptyList()) + NeoForgeDependency(modId).apply(init)

	this@NeoForgeModGenerationOptions.dependencies = dependencies
}
