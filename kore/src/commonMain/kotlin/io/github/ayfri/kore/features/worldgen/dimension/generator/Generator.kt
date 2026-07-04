package io.github.ayfri.kore.features.worldgen.dimension.generator

import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@GeneratedSealedSerializer
@Serializable(with = Generator.Companion.GeneratorSerializer::class)
sealed class Generator {
	companion object {
		@OptIn(InternalSerializationApi::class)
		data object GeneratorSerializer : NamespacedPolymorphicSerializer<Generator>(generatorSealedSerializer())
	}
}
