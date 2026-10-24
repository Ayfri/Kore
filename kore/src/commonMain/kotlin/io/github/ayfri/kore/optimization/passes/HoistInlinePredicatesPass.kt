package io.github.ayfri.kore.optimization.passes

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.features.predicates.conditions.PredicateReference
import io.github.ayfri.kore.features.predicates.predicate
import io.github.ayfri.kore.features.predicates.toCondition
import io.github.ayfri.kore.optimization.DataPackPass
import io.github.ayfri.kore.optimization.utils.InlineSpan
import io.github.ayfri.kore.optimization.utils.Selectors
import io.github.ayfri.kore.optimization.utils.hoistRepeated
import io.github.ayfri.kore.optimization.utils.inlinePredicates

/** The start of an inline predicate condition of an `execute` chain. */
private val PREDICATE = Regex("""\b(?:if|unless) predicate (?=\{)""")

/**
 * Moves the inline predicates repeated across the pack into `predicate` files, named from their content.
 *
 * `execute if predicate {condition:"minecraft:random_chance",chance:0.5f} run say hi` written in two functions becomes
 * `execute if predicate my_pack:generated_scopes/predicate_1a2b3c run say hi` in both, the predicate living once in
 * `data/my_pack/predicate/generated_scopes/predicate_1a2b3c.json`. A predicate is hoisted when it is written inline
 * after an `if` or an `unless` of an `execute` chain, at least [minUses] times in the function lines of the pack. A
 * reference to a predicate stays as it is, and so do the predicates nested in a resource (a loot table, an advancement).
 *
 * Opt-in, since no measurement shows a file reference beating the inline text yet: `this += HoistInlinePredicatesPass()`.
 * The file names are a hash of the predicate text, the same on every build and platform.
 *
 * @property minUses how many times the same predicate must be written before it is hoisted
 *
 * Docs: https://kore.ayfri.com/docs/guides/optimization
 */
data class HoistInlinePredicatesPass(val minUses: Int = 2) : DataPackPass {
	override val name = "hoist-inline-predicates"

	init {
		require(minUses >= 1) { "minUses must be at least 1, got $minUses." }
	}

	override fun run(dataPack: DataPack) = with(dataPack) {
		val predicates = stateOrNull(inlinePredicates).orEmpty()

		hoistRepeated("predicate", minUses, ::locate, { key, fileName ->
			predicates[key]?.takeIf { it.toCondition() !is PredicateReference }?.let { recorded ->
				predicate(fileName) { predicateConditions = recorded.predicateConditions }.asString()
			}
		}) { files, lines -> "hoisted $files inline predicates used by $lines lines into files" }
	}

	private fun locate(line: String): List<InlineSpan> {
		if (!line.startsWith("execute ")) return emptyList()

		val spans = mutableListOf<InlineSpan>()
		var from = 0
		while (true) {
			val start = PREDICATE.find(line, from)?.let { it.range.last + 1 } ?: break
			val end = Selectors.closingBracket(line, start) ?: break
			if (end + 1 < line.length && line[end + 1] != ' ') break

			spans += InlineSpan(line.substring(start, end + 1), start..end)
			from = end + 1
		}

		return spans
	}
}
