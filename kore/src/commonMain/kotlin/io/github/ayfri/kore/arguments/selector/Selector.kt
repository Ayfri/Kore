package io.github.ayfri.kore.arguments.selector

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

	override fun toString() = nbtData.asString().let { arguments ->
		if (arguments.isEmpty()) "@${base.value}" else "@${base.value}[$arguments]"
	}

	companion object {
		/** Parses a selector from its command representation (e.g. `@e[limit=1,tag=!foo]`). */
		fun fromString(value: String): Selector {
			require(value.startsWith("@")) { "A selector must start with '@', got: '$value'" }
			val baseValue = value.removePrefix("@").substringBefore('[')
			val base = SelectorType.entries.firstOrNull { it.value == baseValue }
				?: error("Unknown selector type: '@$baseValue'")
			return Selector(base).apply {
				if ('[' in value) nbtData.copyFrom(
					SelectorArguments.fromString(
						value.substringAfter('[').substringBeforeLast(']')
					)
				)
			}
		}
	}
}
