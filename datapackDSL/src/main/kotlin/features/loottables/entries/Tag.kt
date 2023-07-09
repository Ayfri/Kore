package features.loottables.entries

import arguments.Argument
import features.itemmodifiers.ItemModifier
import features.itemmodifiers.ItemModifierEntry
import features.predicates.conditions.PredicateCondition
import kotlinx.serialization.Serializable

@Serializable
data class Tag(
	var name: Argument.Tag,
	var conditions: List<PredicateCondition>? = null,
	var expand: Boolean? = null,
	var functions: ItemModifier? = null,
	var quality: Int? = null,
	var weight: Int? = null,
) : LootEntry

fun LootEntries.tag(name: Argument.Tag, block: Tag.() -> Unit = {}) {
	add(Tag(name).apply(block))
}

fun Tag.conditions(block: MutableList<PredicateCondition>.() -> Unit) {
	conditions = buildList(block)
}

fun Tag.functions(block: ItemModifierEntry.() -> Unit) {
	functions = ItemModifier().apply {
		modifiers += ItemModifierEntry().apply(block)
	}
}
