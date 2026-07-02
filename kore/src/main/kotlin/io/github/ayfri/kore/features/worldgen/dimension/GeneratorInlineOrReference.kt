package io.github.ayfri.kore.features.worldgen.dimension

import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.generated.NoiseSettings
import io.github.ayfri.kore.serializers.EitherInlineSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KeepGeneratedSerializer
import kotlinx.serialization.Serializable

@OptIn(ExperimentalSerializationApi::class)
@KeepGeneratedSerializer
@Serializable(with = GeneratorInlineOrReference.Companion.GeneratorInlineOrReferenceSerializer::class)
data class GeneratorInlineOrReference(
	val reference: NoiseSettings? = null,
	val inline: Generator? = null,
) {
	companion object {
		data object GeneratorInlineOrReferenceSerializer :
			EitherInlineSerializer<GeneratorInlineOrReference>(generatedSerializer(), "reference", "inline")
	}
}
