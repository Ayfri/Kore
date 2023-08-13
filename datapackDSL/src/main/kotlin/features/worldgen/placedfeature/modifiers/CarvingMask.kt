package features.worldgen.placedfeature.modifiers

import features.worldgen.placedfeature.PlacedFeature
import serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable
data class CarvingMask(
	var step: Step,
) : PlacementModifier()

@Serializable(with = Step.Companion.StepSerializer::class)
enum class Step {
	AIR,
	LIQUID;

	companion object {
		data object StepSerializer : LowercaseSerializer<Step>(entries)
	}
}

fun PlacedFeature.carvingMask(step: Step) {
	placementModifiers += CarvingMask(step)
}
