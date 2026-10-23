package io.github.ayfri.kore.features.contextproviders

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.features.predicates.providers.ConstantContextFloatProvider
import io.github.ayfri.kore.features.predicates.providers.ContextFloatProvider
import io.github.ayfri.kore.features.predicates.providers.ContextFloatProviderReference
import io.github.ayfri.kore.features.predicates.providers.NumberProviderScope
import io.github.ayfri.kore.generated.arguments.types.ContextFloatProviderArgument
import kotlinx.serialization.Transient

/**
 * Data-driven context float provider, usable by ID where the game takes a float provider reference, such as the
 * `cooking_fuel` item component, the villager trades or the `compute` command.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/loot-tables#context-provider-files
 * Minecraft Wiki: https://minecraft.wiki/w/Number_provider
 */
data class ContextFloatProviderFile(
	@Transient
	override var fileName: String = "context_float_provider",
	var provider: ContextFloatProvider,
) : Generator("context_float_provider") {
	override fun generateJson(dataPack: DataPack): String {
		require(provider !is ContextFloatProviderReference) { "Context float provider file '$fileName' must hold an inline provider, not a reference." }
		return dataPack.jsonEncoder.encodeToString(ContextFloatProvider.serializer(), provider)
	}
}

/**
 * Creates a context float provider file holding the provider returned by [block].
 *
 * ```kotlin
 * val chance = contextFloatProvider("compost_chance") {
 *     floatWeightedList {
 *         entry(30, 1f)
 *         entry(70, 0f)
 *     }
 * }
 * ```
 *
 * Produces `data/<namespace>/context_float_provider/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/loot-tables#context-provider-files
 * Minecraft Wiki: https://minecraft.wiki/w/Number_provider
 */
fun DataPack.contextFloatProvider(
	fileName: String = "context_float_provider",
	block: NumberProviderScope.() -> ContextFloatProvider,
): ContextFloatProviderArgument {
	val file = ContextFloatProviderFile(fileName, object : NumberProviderScope {}.block())
	contextFloatProviders += file
	return ContextFloatProviderArgument(fileName, file.namespace ?: name)
}

/**
 * Creates a context float provider file always holding [value], written as a bare number.
 *
 * ```kotlin
 * contextFloatProvider("always_one_and_a_half", 1.5f) // data/<namespace>/context_float_provider/always_one_and_a_half.json: 1.5
 * ```
 *
 * Produces `data/<namespace>/context_float_provider/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/loot-tables#context-provider-files
 * Minecraft Wiki: https://minecraft.wiki/w/Number_provider
 */
fun DataPack.contextFloatProvider(fileName: String = "context_float_provider", value: Float) =
	contextFloatProvider(fileName) { ConstantContextFloatProvider(value) }
