package features.itemmodifiers.functions

import features.itemmodifiers.ItemModifier
import features.predicates.PredicateAsList
import serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable
data class CopyName(
	override var conditions: PredicateAsList? = null,
	val source: Source,
) : ItemFunction()

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
	modifiers += CopyName(source = source).apply(block)
}
