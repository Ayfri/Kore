package features.worldgen.structures

import serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = GenerationStep.Companion.GenerationStepSerializer::class)
enum class GenerationStep {
	RAW_GENERATION,
	LAKES,
	LOCAL_MODIFICATIONS,
	UNDERGROUND_STRUCTURES,
	SURFACE_STRUCTURES,
	STRONGHOLDS,
	UNDERGROUND_ORES,
	UNDERGROUND_DECORATION,
	VEGETAL_DECORATION,
	TOP_LAYER_MODIFICATION;

	companion object {
		data object GenerationStepSerializer : LowercaseSerializer<GenerationStep>(entries)
	}
}
