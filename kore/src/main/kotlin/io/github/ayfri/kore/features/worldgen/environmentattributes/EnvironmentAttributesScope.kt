package io.github.ayfri.kore.features.worldgen.environmentattributes

import io.github.ayfri.kore.generated.arguments.types.EnvironmentAttributeArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(EnvironmentAttributesScope.Companion.EnvironmentAttributesScopeSerializer::class)
data class EnvironmentAttributesScope(
	val attributes: MutableMap<EnvironmentAttributeArgument, EnvironmentAttributeValue> = mutableMapOf(),
) {
	operator fun get(name: EnvironmentAttributeArgument) = attributes[name]

	operator fun set(name: EnvironmentAttributeArgument, value: EnvironmentAttributeValue) {
		attributes[name] = value
	}

	companion object {
		data object EnvironmentAttributesScopeSerializer : InlineAutoSerializer<EnvironmentAttributesScope>(
			EnvironmentAttributesScope::class
		)
	}
}
