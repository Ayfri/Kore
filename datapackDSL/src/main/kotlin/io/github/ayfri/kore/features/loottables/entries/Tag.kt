package io.github.ayfri.kore.features.loottables.entries

import io.github.ayfri.kore.arguments.types.resources.TagArgument
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.itemmodifiers.ItemModifierAsList
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

@Serializable
data class Tag(
	var name: TagArgument,
	var conditions: PredicateAsList? = null,
	var expand: Boolean? = null,
	var functions: ItemModifierAsList? = null,
	var quality: Int? = null,
	var weight: Int? = null,
) : LootEntry()

fun LootEntries.tag(name: TagArgument, block: Tag.() -> Unit = {}) {
	add(Tag(name).apply(block))
}

fun Tag.conditions(block: Predicate.() -> Unit) {
	conditions = Predicate().apply(block)
}

fun Tag.functions(block: ItemModifier.() -> Unit) {
	functions = ItemModifier().apply(block)
}
