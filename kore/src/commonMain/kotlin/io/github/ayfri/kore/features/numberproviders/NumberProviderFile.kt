package io.github.ayfri.kore.features.numberproviders

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.features.predicates.providers.ConstantNumberProvider
import io.github.ayfri.kore.features.predicates.providers.NumberProvider
import io.github.ayfri.kore.features.predicates.providers.NumberProviderScope
import io.github.ayfri.kore.generated.arguments.types.NumberProviderArgument
import kotlinx.serialization.Transient

/**
 * Data-driven number provider, usable by ID where the game takes a number provider reference, such as the
 * `compostable` item component.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/loot-tables#number-provider-files
 * Minecraft Wiki: https://minecraft.wiki/w/Number_provider
 */
data class NumberProviderFile(
	@Transient
	override var fileName: String = "number_provider",
	var provider: NumberProvider,
) : Generator("number_provider") {
	override fun generateJson(dataPack: DataPack) =
		dataPack.jsonEncoder.encodeToString(NumberProvider.serializer(), provider)
}

/**
 * Creates a number provider file holding the provider returned by [block].
 *
 * ```kotlin
 * val chance = numberProvider("compost_chance") {
 *     weightedListNumber {
 *         entry(30, 1f)
 *         entry(70, 0f)
 *     }
 * }
 * ```
 *
 * Produces `data/<namespace>/number_provider/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/loot-tables#number-provider-files
 * Minecraft Wiki: https://minecraft.wiki/w/Number_provider
 */
fun DataPack.numberProvider(
	fileName: String = "number_provider",
	block: NumberProviderScope.() -> NumberProvider,
): NumberProviderArgument {
	val file = NumberProviderFile(fileName, object : NumberProviderScope {}.block())
	numberProviders += file
	return NumberProviderArgument(fileName, file.namespace ?: name)
}

/**
 * Creates a number provider file always holding [value], written as a bare number.
 *
 * ```kotlin
 * numberProvider("always_add_one", 1f) // data/<namespace>/number_provider/always_add_one.json: 1.0
 * ```
 *
 * Produces `data/<namespace>/number_provider/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/loot-tables#number-provider-files
 * Minecraft Wiki: https://minecraft.wiki/w/Number_provider
 */
fun DataPack.numberProvider(fileName: String = "number_provider", value: Float) =
	numberProvider(fileName) { ConstantNumberProvider(value) }
