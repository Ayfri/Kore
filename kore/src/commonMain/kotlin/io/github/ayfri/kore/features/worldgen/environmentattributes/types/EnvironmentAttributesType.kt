package io.github.ayfri.kore.features.worldgen.environmentattributes.types

import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@GeneratedSealedSerializer
@Serializable(with = EnvironmentAttributesType.Companion.EnvironmentAttributesTypeSerializer::class)
sealed class EnvironmentAttributesType {
	companion object {
		data object EnvironmentAttributesTypeSerializer : NamespacedPolymorphicSerializer<EnvironmentAttributesType>(
			environmentAttributesTypeSealedSerializer(),
			skipOutputName = true,
			outputName = "__type__",
		)
	}
}
