package io.github.ayfri.kore.optimization.passes

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.features.contextproviders.contextFloatProvider
import io.github.ayfri.kore.features.contextproviders.contextIntProvider
import io.github.ayfri.kore.optimization.DataPackPass
import io.github.ayfri.kore.optimization.utils.InlineSpan
import io.github.ayfri.kore.optimization.utils.Selectors
import io.github.ayfri.kore.optimization.utils.hoistRepeated
import io.github.ayfri.kore.optimization.utils.inlineFloatProviders
import io.github.ayfri.kore.optimization.utils.inlineIntProviders

/** The mode and the start of the inline provider of a `compute`, or of a `data modify` with a `compute` source. */
private val COMPUTE = Regex(
	"""^(?:execute .*\brun )?(?:compute |data modify (?:storage \S+|entity \S+|block \S+ \S+ \S+) \S+ (?:set|append|merge|prepend|insert -?\d+) compute )""" +
		"""(?:default|block \S+ \S+ \S+|entity \S+) (integer|float) (?=\{)"""
)

/**
 * Moves the inline context providers repeated across the pack into `context_int_provider` and `context_float_provider`
 * files, named from their content.
 *
 * `compute default integer {type:"minecraft:add",...}` written in two functions becomes
 * `compute default integer my_pack:generated_scopes/provider_1a2b3c` in both, the provider living once in
 * `data/my_pack/context_int_provider/generated_scopes/provider_1a2b3c.json`. A provider is hoisted when it is the whole
 * provider argument of a `compute` command or of a `data modify ... compute` source, written inline in at least
 * [minUses] function lines. Constants and references stay as they are, and so do the providers nested in a resource
 * (a loot table, an item modifier).
 *
 * Opt-in, a file reference measured 0 to 25 % cheaper than the inline text on big trees and equal on small ones:
 * `this += HoistContextProvidersPass()`.
 * The file names are a hash of the provider text, the same on every build and platform.
 *
 * @property minUses how many lines must use the same provider before it is hoisted
 *
 * Docs: https://kore.ayfri.com/docs/guides/optimization
 */
data class HoistContextProvidersPass(val minUses: Int = 2) : DataPackPass {
	override val name = "hoist-context-providers"

	init {
		require(minUses >= 1) { "minUses must be at least 1, got $minUses." }
	}

	override fun run(dataPack: DataPack) = with(dataPack) {
		val ints = stateOrNull(inlineIntProviders).orEmpty()
		val floats = stateOrNull(inlineFloatProviders).orEmpty()

		hoistRepeated("provider", minUses, ::locate, { key, fileName ->
			val (mode, snbt) = key.split(' ', limit = 2)
			when (mode) {
				"integer" -> ints[snbt]?.let { provider -> contextIntProvider(fileName) { provider }.asString() }
				else -> floats[snbt]?.let { provider -> contextFloatProvider(fileName) { provider }.asString() }
			}
		}) { files, lines -> "hoisted $files inline context providers used by $lines lines into files" }
	}

	private fun locate(line: String): List<InlineSpan> {
		val match = COMPUTE.find(line) ?: return emptyList()
		val start = match.range.last + 1
		val end = Selectors.closingBracket(line, start) ?: return emptyList()
		if (end + 1 < line.length && line[end + 1] != ' ') return emptyList()
		return listOf(InlineSpan("${match.groupValues[1]} ${line.substring(start, end + 1)}", start..end))
	}
}
