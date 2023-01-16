package arguments.selector

import arguments.Argument
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonPrimitive
import serializers.ToStringSerializer

@Serializable(Advancement.Companion.AdvancementSerializer::class)
data class Advancement(val advancement: Argument.Advancement, val done: Boolean = false, val criteria: Map<String, Boolean> = emptyMap()) {
	companion object {
		object AdvancementSerializer : ToStringSerializer<Advancement>() {
			override fun serialize(encoder: Encoder, value: Advancement) {
				val criteria = when {
					value.criteria.isNotEmpty() -> value.criteria.entries.joinToString(",", "{", "}") { "${it.key}=${it.value}" }
					else -> ""
				}
				encoder.encodeString("${value.advancement.asString()}=${criteria.ifEmpty { value.done }}")
			}
		}
	}
}

@Serializable(Advancements.Companion.AdvancementsSerializer::class)
data class Advancements(val advancements: Set<Advancement> = emptySet()) {
	companion object {
		object AdvancementsSerializer : ToStringSerializer<Advancements>() {
			override fun serialize(encoder: Encoder, value: Advancements) {
				encoder.encodeString(value.advancements.joinToString(",", "{", "}") {
					json.encodeToJsonElement(it).jsonPrimitive.content
				})
			}
		}
	}
}

class AdvancementBuilder {
	private val advancements = mutableSetOf<Advancement>()

	fun advancement(advancement: Argument.Advancement, done: Boolean = true, block: MutableMap<String, Boolean>.() -> Unit = {}) {
		val criteria = buildMap(block)
		advancements.add(Advancement(advancement, done, criteria))
	}

	operator fun <T : Argument.Advancement> T.invoke(done: Boolean = true, block: MutableMap<String, Boolean>.() -> Unit = {}) {
		val criteria = buildMap(block)
		advancements.add(Advancement(this, done, criteria))
	}

	fun build() = Advancements(advancements)
}
