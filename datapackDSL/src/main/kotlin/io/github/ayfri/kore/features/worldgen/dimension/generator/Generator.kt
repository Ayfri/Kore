package io.github.ayfri.kore.features.worldgen.dimension.generator

import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = Generator.Companion.GeneratorSerializer::class)
sealed class Generator {
	companion object {
		data object GeneratorSerializer : NamespacedPolymorphicSerializer<Generator>(Generator::class)
	}
}
