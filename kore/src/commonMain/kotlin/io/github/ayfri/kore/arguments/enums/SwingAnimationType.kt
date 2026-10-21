package io.github.ayfri.kore.arguments.enums

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

/** The arm-swing animation of an item or of the `swing` command, `whack` when omitted. */
@Serializable(with = SwingAnimationType.Companion.SwingAnimationTypeSerializer::class)
enum class SwingAnimationType {
	STAB,
	WHACK;

	companion object {
		data object SwingAnimationTypeSerializer : LowercaseSerializer<SwingAnimationType>(entries)
	}
}
