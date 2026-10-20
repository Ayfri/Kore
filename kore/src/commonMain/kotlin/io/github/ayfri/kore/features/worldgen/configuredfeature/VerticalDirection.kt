package io.github.ayfri.kore.features.worldgen.configuredfeature

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

/** A vertical direction, used by the features and placement modifiers that walk up or down. */
@Serializable(with = VerticalDirection.Companion.VerticalDirectionSerializer::class)
enum class VerticalDirection {
	DOWN,
	UP;

	companion object {
		data object VerticalDirectionSerializer : LowercaseSerializer<VerticalDirection>(entries)
	}
}
