package io.github.ayfri.kore.features.worldgen.dimension

import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.generated.NoiseSettings
import io.github.ayfri.kore.serializers.EitherInlineSerializer
import kotlinx.serialization.Serializable

@Serializable(with = GeneratorInlineOrReference.Companion.GeneratorInlineOrReferenceSerializer::class)
data class GeneratorInlineOrReference(
	val reference: NoiseSettings? = null,
	val inline: Generator? = null,
) {
	companion object {
		data object GeneratorInlineOrReferenceSerializer : EitherInlineSerializer<GeneratorInlineOrReference>(
			GeneratorInlineOrReference::class,
			GeneratorInlineOrReference::reference,
			GeneratorInlineOrReference::inline
		)
	}
}
