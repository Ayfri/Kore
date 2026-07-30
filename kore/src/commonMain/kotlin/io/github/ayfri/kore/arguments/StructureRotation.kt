package io.github.ayfri.kore.arguments

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

/**
 * Represents a rotation applied to a structure, either in the GameTest framework or in world generation.
 */
@Serializable(StructureRotation.Companion.RotationSerializer::class)
enum class StructureRotation {
	/** No rotation applied - structure spawns in its original orientation */
	NONE,

	/** 90-degree clockwise rotation from the original orientation */
	CLOCKWISE_90,

	/** 180-degree rotation from the original orientation */
	ROT_180,

	/** 90-degree counterclockwise rotation from the original orientation */
	COUNTERCLOCKWISE_90;

	companion object {
		data object RotationSerializer : LowercaseSerializer<StructureRotation>(entries, transform = {
			if (this == ROT_180) "180" else name.lowercase()
		})
	}
}
