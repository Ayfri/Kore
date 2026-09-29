package io.github.ayfri.kore.arguments.selector

import io.github.ayfri.kore.arguments.Advancement
import io.github.ayfri.kore.generated.arguments.types.AdvancementArgument
import io.github.ayfri.kore.serializers.ToStringSerializer
import kotlinx.serialization.Serializable

/**
 * Wrapper for advancement filters used in selectors (maps to the `advancements` selector argument).
 *
 * See the project documentation for advancements: [Advancements](https://kore.ayfri.com/docs/data-driven/advancements).
 */
@Serializable(with = SelectorAdvancements.Companion.SelectorAdvancementsSerializer::class)
data class SelectorAdvancements(val advancements: Set<Advancement> = emptySet()) {
	/** The advancements as written in a selector, e.g. `{minecraft:story/enchant_item={bar=true}}`. */
	fun asString() = advancements.joinToString(",", "{", "}") { it.selectorString() }

	companion object {
		data object SelectorAdvancementsSerializer : ToStringSerializer<SelectorAdvancements>({ asString() })

		data object SelectorAdvancementSerializer : ToStringSerializer<Advancement>({ selectorString() })
	}
}

private fun Advancement.selectorString() = when {
	criteria.isEmpty() -> "${advancement.asString()}=$done"
	else -> "${advancement.asString()}=${criteria.entries.joinToString(",", "{", "}") { "${it.key}=${it.value}" }}"
}

/**
 * Builder helper for constructing `SelectorAdvancements` instances in a DSL style.
 */
class AdvancementBuilder {
	private val advancements = mutableSetOf<Advancement>()

	/** Add an advancement filter to this builder. */
	fun advancement(
		advancement: AdvancementArgument,
		done: Boolean = true,
		block: MutableMap<String, Boolean>.() -> Unit = {},
	) {
		val criteria = buildMap(block)
		advancements.add(Advancement(advancement, done, criteria))
	}

	/** DSL-style invocation for an `AdvancementArgument`. */
	operator fun <T : AdvancementArgument> T.invoke(
		done: Boolean = true,
		block: MutableMap<String, Boolean>.() -> Unit = {},
	) {
		val criteria = buildMap(block)
		advancements.add(Advancement(this, done, criteria))
	}

	/** Build the immutable `SelectorAdvancements` value. */
	fun build() = SelectorAdvancements(advancements)
}
