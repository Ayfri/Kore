package features.worldgen.dimension.generator

import kotlinx.serialization.Serializable
import serializers.NamespacedPolymorphicSerializer

@Serializable(with = Generator.Companion.GeneratorSerializer::class)
sealed class Generator {
	companion object {
		data object GeneratorSerializer : NamespacedPolymorphicSerializer<Generator>(Generator::class)
	}
}
