package io.github.ayfri.kore.features.contextproviders

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.features.predicates.providers.ConstantContextIntProvider
import io.github.ayfri.kore.features.predicates.providers.ContextIntProvider
import io.github.ayfri.kore.features.predicates.providers.ContextIntProviderReference
import io.github.ayfri.kore.features.predicates.providers.NumberProviderScope
import io.github.ayfri.kore.generated.arguments.types.ContextIntProviderArgument
import kotlinx.serialization.Transient

/**
 * Data-driven context int provider, usable by ID where the game takes an int provider reference, such as the
 * `compostable` item component, the villager trades or the `compute` command.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/loot-tables#context-provider-files
 * Minecraft Wiki: https://minecraft.wiki/w/Number_provider
 */
data class ContextIntProviderFile(
	@Transient
	override var fileName: String = "context_int_provider",
	var provider: ContextIntProvider,
) : Generator("context_int_provider") {
	override fun generateJson(dataPack: DataPack): String {
		require(provider !is ContextIntProviderReference) { "Context int provider file '$fileName' must hold an inline provider, not a reference." }
		return dataPack.jsonEncoder.encodeToString(ContextIntProvider.serializer(), provider)
	}
}

/**
 * Creates a context int provider file holding the provider returned by [block].
 *
 * ```kotlin
 * val layers = contextIntProvider("compost_layers") {
 *     intWeightedList {
 *         entry(30, 1)
 *         entry(70, 0)
 *     }
 * }
 * ```
 *
 * Produces `data/<namespace>/context_int_provider/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/loot-tables#context-provider-files
 * Minecraft Wiki: https://minecraft.wiki/w/Number_provider
 */
fun DataPack.contextIntProvider(
	fileName: String = "context_int_provider",
	block: NumberProviderScope.() -> ContextIntProvider,
): ContextIntProviderArgument {
	val file = ContextIntProviderFile(fileName, object : NumberProviderScope {}.block())
	contextIntProviders += file
	return ContextIntProviderArgument(fileName, file.namespace ?: name)
}

/**
 * Creates a context int provider file always holding [value], written as a bare number.
 *
 * ```kotlin
 * contextIntProvider("always_one", 1) // data/<namespace>/context_int_provider/always_one.json: 1
 * ```
 *
 * Produces `data/<namespace>/context_int_provider/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/loot-tables#context-provider-files
 * Minecraft Wiki: https://minecraft.wiki/w/Number_provider
 */
fun DataPack.contextIntProvider(fileName: String = "context_int_provider", value: Int) =
	contextIntProvider(fileName) { ConstantContextIntProvider(value) }
