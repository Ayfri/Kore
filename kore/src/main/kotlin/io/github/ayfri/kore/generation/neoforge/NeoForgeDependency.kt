package io.github.ayfri.kore.generation.neoforge

import io.github.ayfri.kore.generation.forge.Ordering
import io.github.ayfri.kore.generation.forge.Side
import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable


@Serializable(with = NeoForgeDependencyType.Companion.NeoForgeDependencyTypeSerializer::class)
enum class NeoForgeDependencyType {
	REQUIRED,
	OPTIONAL,
	INCOMPATIBLE,
	DISCOURAGED;

	companion object {
		data object NeoForgeDependencyTypeSerializer : LowercaseSerializer<NeoForgeDependencyType>(entries)
	}
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

context(options: NeoForgeModGenerationOptions)
fun NeoForgeModProperties.dependency(modId: String, init: NeoForgeDependency.() -> Unit = {}) {
	val dependencies = (options.dependencies ?: emptyMap()).toMutableMap()
	dependencies[this.modId] = (dependencies[this.modId] ?: emptyList()) + NeoForgeDependency(modId).apply(init)

	options.dependencies = dependencies
}
