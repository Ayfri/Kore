package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

@Serializable
data class FillPlayerHead(
	override var conditions: PredicateAsList? = null,
	val entity: Source,
) : ItemFunction()

fun ItemModifier.fillPlayerHead(entity: Source, block: FillPlayerHead.() -> Unit = {}) {
	modifiers += FillPlayerHead(entity = entity).apply(block)
}
