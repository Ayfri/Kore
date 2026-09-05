package io.github.ayfri.kore.optimization.passes

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.optimization.DataPackPass
import io.github.ayfri.kore.optimization.PassResult
import io.github.ayfri.kore.optimization.utils.Selectors
import io.github.ayfri.kore.optimization.utils.rewriteLines

/**
 * Sorts the arguments of every selector so the cheapest filters run first.
 *
 * The game tests selector arguments in the written order, so an `@e[nbt={...},type=marker]` reads the NBT of every
 * entity in range before checking the type, while `@e[type=marker,nbt={...}]` reads it for markers only. The result
 * set is identical either way, which makes the reordering free.
 *
 * Docs: https://kore.ayfri.com/docs/guides/optimization
 */
data object ReorderSelectorArgumentsPass : DataPackPass {
	/** Cheapest first: a type check is a field read, a predicate runs a whole loot condition, NBT deserializes the entity. */
	private val priorities = listOf(
		"type", "tag", "team", "scores", "level", "gamemode", "name", "limit", "sort",
		"distance", "x", "y", "z", "dx", "dy", "dz", "x_rotation", "y_rotation",
		"advancements", "predicate", "nbt",
	).withIndex().associate { (index, key) -> key to index }

	private val unknownPriority = priorities.getValue("advancements")

	override val name = "reorder-selector-arguments"

	override fun run(dataPack: DataPack) = dataPack.rewriteLines(::reorder) { "reordered the selectors of $it lines" }

	internal fun reorder(line: String): String? {
		if (line.trimStart().startsWith('#')) return null

		return Selectors.rewrite(line) { arguments ->
			val sorted = arguments.sortedBy { priorities[it.substringBefore('=').trim()] ?: unknownPriority }
			sorted.takeIf { it != arguments }
		}
	}
}
