package features.itemmodifiers.functions

import features.itemmodifiers.ItemModifier
import kotlinx.serialization.Serializable
import serializers.LowercaseSerializer

@Serializable
data class CopyName(val source: Source) : ItemFunction()

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

fun ItemModifier.copyName(source: Source, block: CopyName.() -> Unit = {}) {
	modifiers += CopyName(source).apply(block)
}
