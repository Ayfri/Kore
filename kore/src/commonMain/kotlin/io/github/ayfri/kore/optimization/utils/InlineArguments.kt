package io.github.ayfri.kore.optimization.utils

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.DataPackStateKey
import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.features.predicates.providers.ContextFloatProvider
import io.github.ayfri.kore.features.predicates.providers.ContextIntProvider

internal val inlineFloatProviders = DataPackStateKey<MutableMap<String, ContextFloatProvider>>("kore.inlineFloatProviders")
internal val inlineIntProviders = DataPackStateKey<MutableMap<String, ContextIntProvider>>("kore.inlineIntProviders")

/**
 * An SNBT argument written from [value], which the pack keeps under its [snbt] text in the map stored at [key] once a
 * command holding it is added to a function, so a pass can turn it into a file without parsing the text back.
 */
internal class InlineArgument<T : Any>(
	val value: T,
	val snbt: String,
	private val key: DataPackStateKey<MutableMap<String, T>>,
) : Argument {
	override fun asString() = snbt

	fun record(dataPack: DataPack) {
		val recorded = dataPack.state(key) { linkedMapOf() }
		if (snbt !in recorded) recorded[snbt] = value
	}
}
