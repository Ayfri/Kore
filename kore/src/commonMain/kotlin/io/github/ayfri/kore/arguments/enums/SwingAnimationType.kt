package io.github.ayfri.kore.arguments.enums

import io.github.ayfri.kore.arguments.EnumArgument
import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

/** The arm-swing animation of an item or of the `swing` command, `whack` when omitted, `none` plays no animation. */
@Serializable(with = SwingAnimationType.Companion.SwingAnimationTypeSerializer::class)
enum class SwingAnimationType : EnumArgument {
	NONE,
	STAB,
	WHACK;

	companion object {
		data object SwingAnimationTypeSerializer : LowercaseSerializer<SwingAnimationType>(entries)
	}
}
