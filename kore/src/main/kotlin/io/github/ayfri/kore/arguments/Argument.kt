package io.github.ayfri.kore.arguments

import io.github.ayfri.kore.serializers.ToStringSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Encoder

@Serializable(Argument.ArgumentSerializer::class)
interface Argument {
	fun asString(): String

	data object ArgumentSerializer : ToStringSerializer<Argument>() {
		override fun serialize(encoder: Encoder, value: Argument) = encoder.encodeString(value.asString())
	}
}
