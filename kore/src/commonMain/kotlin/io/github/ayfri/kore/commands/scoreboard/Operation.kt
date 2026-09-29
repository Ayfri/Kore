package io.github.ayfri.kore.commands.scoreboard

import io.github.ayfri.kore.arguments.EnumArgument
import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(Operation.Companion.OperationSerializer::class)
enum class Operation(val symbol: String) : EnumArgument {
	ADD("+="),
	REMOVE("-="),
	SET("="),
	MULTIPLY("*="),
	DIVIDE("/="),
	MODULO("%="),
	SWAP("><"),
	MIN("<"),
	MAX(">");

	override fun asString() = symbol

	companion object {
		data object OperationSerializer : LowercaseSerializer<Operation>(entries, { asString() })
	}
}
