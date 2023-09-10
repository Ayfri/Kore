package io.github.ayfri.kore.arguments.selector

import io.github.ayfri.kore.arguments.Advancement
import io.github.ayfri.kore.arguments.types.resources.AdvancementArgument
import io.github.ayfri.kore.serializers.ToStringSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.jsonPrimitive

@Serializable(with = SelectorAdvancements.Companion.SelectorAdvancementsSerializer::class)
data class SelectorAdvancements(val advancements: Set<Advancement> = emptySet()) {
	companion object {
		object SelectorAdvancementsSerializer : ToStringSerializer<SelectorAdvancements>({ encoder ->
			require(encoder is JsonEncoder) { "SelectorAdvancements can only be serialized as Json" }
			advancements.joinToString(",", "{", "}") {
				encoder.json.encodeToJsonElement(SelectorAdvancementSerializer, it).jsonPrimitive.content
			}
		})

		object SelectorAdvancementSerializer : ToStringSerializer<Advancement>({
			val criteria = when {
				criteria.isNotEmpty() -> criteria.entries.joinToString(",", "{", "}") { "${it.key}=${it.value}" }
				else -> ""
			}

			"${advancement.asString()}=${criteria.ifEmpty { done }}"
		})
	}
}

class AdvancementBuilder {
	private val advancements = mutableSetOf<Advancement>()

	fun advancement(
		advancement: AdvancementArgument,
		done: Boolean = true,
		block: MutableMap<String, Boolean>.() -> Unit = {},
	) {
		val criteria = buildMap(block)
		advancements.add(Advancement(advancement, done, criteria))
	}

	operator fun <T : AdvancementArgument> T.invoke(
		done: Boolean = true,
		block: MutableMap<String, Boolean>.() -> Unit = {},
	) {
		val criteria = buildMap(block)
		advancements.add(Advancement(this, done, criteria))
	}

	fun build() = SelectorAdvancements(advancements)
}
