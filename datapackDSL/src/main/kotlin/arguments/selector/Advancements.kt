package arguments.selector

import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonPrimitive
import serializers.ToStringSerializer

@Serializable(Advancement.Companion.AdvancementSerializer::class)
data class Advancement(val name: String, val namespace: String? = null, val done: Boolean = false, val criteria: Map<String, Boolean> = emptyMap()) {
	companion object {
		object AdvancementSerializer : ToStringSerializer<Advancement>() {
			override fun serialize(encoder: Encoder, value: Advancement) {
				val criteria = if (value.criteria.isNotEmpty()) value.criteria.entries.joinToString(",", "{", "}") { "${it.key}=${it.value}" } else ""
				encoder.encodeString("${value.namespace?.let { "$it:" } ?: ""}${value.name}=${criteria.ifEmpty { value.done }}")
			}
		}
	}
}

@Serializable(Advancements.Companion.AdvancementsSerializer::class)
data class Advancements(val advancements: Map<String, Advancement> = emptyMap()) {
	companion object {
		object AdvancementsSerializer : ToStringSerializer<Advancements>() {
			override fun serialize(encoder: Encoder, value: Advancements) {
				encoder.encodeString(value.advancements.entries.joinToString(",", "{", "}") { json.encodeToJsonElement(it.value).jsonPrimitive.content })
			}
		}
	}
}

class AdvancementBuilder {
	private val advancements = mutableMapOf<String, Advancement>()

	fun advancement(name: String, namespace: String? = null, done: Boolean = true, block: MutableMap<String, Boolean>.() -> Unit = {}) {
		val criteria = buildMap(block)
		advancements[name] = Advancement(name, namespace, done, criteria)
	}

	fun build() = Advancements(advancements)
}
