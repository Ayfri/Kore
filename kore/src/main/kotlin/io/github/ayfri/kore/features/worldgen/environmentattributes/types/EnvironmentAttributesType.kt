package io.github.ayfri.kore.features.worldgen.environmentattributes.types

import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = EnvironmentAttributesType.Companion.EnvironmentAttributesTypeSerializer::class)
sealed class EnvironmentAttributesType {
	companion object {
		data object EnvironmentAttributesTypeSerializer : NamespacedPolymorphicSerializer<EnvironmentAttributesType>(
			EnvironmentAttributesType::class,
			skipOutputName = true,
			outputName = "__type__",
		)
	}
}
