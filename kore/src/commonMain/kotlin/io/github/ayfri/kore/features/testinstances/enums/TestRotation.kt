package io.github.ayfri.kore.features.testinstances.enums

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

/**
 * Represents the rotation options for test structures in Minecraft's GameTest framework.
 *
 * Test structures can be rotated during execution to test functionality from different orientations.
 * This is particularly useful for testing directional blocks, redstone circuits, or mob pathfinding
 * that may behave differently based on structure orientation.
 */
@Serializable(TestRotation.Companion.TestRotationSerializer::class)
enum class TestRotation {
	/** No rotation applied - structure spawns in its original orientation */
	NONE,

	/** 90-degree clockwise rotation from the original orientation */
	CLOCKWISE_90,

	/** 180-degree rotation from the original orientation */
	ROT_180,

	/** 90-degree counterclockwise rotation from the original orientation */
	COUNTERCLOCKWISE_90;

	companion object {
		data object TestRotationSerializer : LowercaseSerializer<TestRotation>(entries)
	}
}
