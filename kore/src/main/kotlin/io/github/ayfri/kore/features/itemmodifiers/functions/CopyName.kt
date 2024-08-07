package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable
data class CopyName(
	override var conditions: PredicateAsList? = null,
	val source: Source,
) : ItemFunction()

@Serializable(with = Source.Companion.SourceSerializer::class)
enum class Source {
	BLOCK_ENTITY,
	ATTACKING_ENTITY,
	LAST_DAMAGE_PLAYER,
	THIS;

	companion object {
		data object SourceSerializer : LowercaseSerializer<Source>(entries)
	}
}

fun ItemModifier.copyName(source: Source, block: CopyName.() -> Unit = {}) {
	modifiers += CopyName(source = source).apply(block)
}
