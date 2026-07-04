package io.github.ayfri.kore.commands.scoreboard

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Encoder

@Serializable(Operation.Companion.OperationSerializer::class)
enum class Operation(val symbol: String) {
	ADD("+="),
	REMOVE("-="),
	SET("="),
	MULTIPLY("*="),
	DIVIDE("/="),
	MODULO("%="),
	SWAP("><"),
	MIN("<"),
	MAX(">");

	companion object {
		data object OperationSerializer : LowercaseSerializer<Operation>(entries) {
			override fun serialize(encoder: Encoder, value: Operation) = encoder.encodeString(value.symbol)
		}
	}
}
