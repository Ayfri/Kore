package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

@Serializable
data class SetItem(
	override var conditions: PredicateAsList? = null,
	var item: ItemArgument,
) : ItemFunction()

fun ItemModifier.setItem(item: ItemArgument, block: SetItem.() -> Unit = {}) =
	SetItem(item = item).apply(block).also { modifiers += it }
