package io.github.ayfri.kore.arguments.selector

import io.github.ayfri.kore.utils.unescape
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.ClassDiscriminatorMode
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import kotlinx.serialization.json.encodeToJsonElement

@OptIn(ExperimentalSerializationApi::class)
internal val json = Json {
	allowStructuredMapKeys = true
	classDiscriminatorMode = ClassDiscriminatorMode.NONE
	explicitNulls = false
	ignoreUnknownKeys = true
	namingStrategy = JsonNamingStrategy.SnakeCase
}

/**
 * Represents a selector variable with optional selector arguments.
 *
 * Example: `Selector(SelectorType.NEAREST_PLAYER)` -> `@p`, or with arguments -> `@p[distance=..]`.
 * See the Minecraft wiki on target selectors: [Target selectors](https://minecraft.wiki/w/Target_selectors).
 */
data class Selector(val base: SelectorType) {
	/** Mutable container of selector arguments for this selector. */
	val nbtData = SelectorArguments()
	/** Whether this selector targets players. */
	val isPlayer get() = base.isPlayer

	override fun toString() = when {
		nbtData == SelectorArguments() -> "@${base.value}"
		else -> "@${base.value}[${json.encodeToJsonElement(nbtData).toString().unescape()}]"
	}
}
