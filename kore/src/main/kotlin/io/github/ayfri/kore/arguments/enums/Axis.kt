package io.github.ayfri.kore.arguments.enums

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(Axis.Companion.AxisSerializer::class)
enum class Axis {
	X,
	Y,
	Z;

	companion object {
		data object AxisSerializer : LowercaseSerializer<Axis>(entries)
	}
}
