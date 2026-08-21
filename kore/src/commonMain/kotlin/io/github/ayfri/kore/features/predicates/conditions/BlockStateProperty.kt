package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.features.predicates.Predicate
import kotlinx.serialization.Serializable

/**
 * Passes when the block of the loot context is [block] and all its [properties] match.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/predicates
 * Minecraft Wiki: [Predicate - block_state_property](https://minecraft.wiki/w/Predicate#block_state_property)
 */
@Serializable
data class BlockStateProperty(
	var block: BlockArgument,
	var properties: Map<String, String>? = null,
) : PredicateCondition()

/** Adds a [BlockStateProperty] condition matching [block] and its [properties]. */
fun Predicate.blockStateProperty(block: BlockArgument, properties: Map<String, String>? = null) {
	predicateConditions += BlockStateProperty(block, properties)
}

/** Adds a [BlockStateProperty] condition matching [block] and the properties declared in [properties]. */
fun Predicate.blockStateProperty(block: BlockArgument, properties: MutableMap<String, String>.() -> Unit) {
	predicateConditions += BlockStateProperty(block, buildMap(properties))
}
