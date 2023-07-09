package features.itemmodifiers.functions

import kotlinx.serialization.Serializable
import serializers.LowercaseSerializer

@Serializable
data class CopyName(val source: Source) : ItemFunctionSurrogate

@Serializable(with = Source.Companion.SourceSerializer::class)
enum class Source {
	BLOCK_ENTITY,
	KILLER,
	KILLER_PLAYER,
	THIS;

	companion object {
		data object SourceSerializer : LowercaseSerializer<Source>(entries)
	}
}
