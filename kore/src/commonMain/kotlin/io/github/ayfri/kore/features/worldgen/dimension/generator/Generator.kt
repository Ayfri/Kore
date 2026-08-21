package io.github.ayfri.kore.features.worldgen.dimension.generator

import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/**
 * Decides how the terrain of a dimension is built, serialized as `{ "type": "<generator>", ... }`.
 *
 * Set it through one of the builders scoped to a dimension: [debugGenerator], [flatGenerator] or [noiseGenerator].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_dimension
 */
@GeneratedSealedSerializer
@Serializable(with = Generator.Companion.GeneratorSerializer::class)
sealed class Generator {
	companion object {
		@OptIn(InternalSerializationApi::class)
		data object GeneratorSerializer : NamespacedPolymorphicSerializer<Generator>(generatorSealedSerializer())
	}
}
