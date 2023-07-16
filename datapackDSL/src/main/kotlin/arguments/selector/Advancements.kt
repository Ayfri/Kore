package arguments.selector

import arguments.types.resources.AdvancementArgument
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.jsonPrimitive
import serializers.ToStringSerializer

@Serializable(Advancement.Companion.DefaultAdvancementSerializer::class)
data class Advancement(
	val advancement: AdvancementArgument,
	val done: Boolean = false,
	val criteria: Map<String, Boolean> = emptyMap()
) {
	companion object {
		object DefaultAdvancementSerializer : ToStringSerializer<Advancement>() {
			override val descriptor = buildClassSerialDescriptor("Advancement") {
				element<String>("advancement")
				element<Boolean>("done")
				element<Map<String, Boolean>>("criteria")
			}

			override fun serialize(encoder: Encoder, value: Advancement) {
				encoder.encodeStructure(descriptor) {
					encodeStringElement(descriptor, 0, value.advancement.asString())
					encodeBooleanElement(descriptor, 1, value.done)
					encodeSerializableElement(descriptor, 2, MapSerializer(String.serializer(), Boolean.serializer()), value.criteria)
				}
			}
		}

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

@Serializable
data class Advancements(val advancements: Set<@Contextual Advancement> = emptySet()) {
	companion object {
		object AdvancementsSerializer : ToStringSerializer<Advancements>() {
			override fun serialize(encoder: Encoder, value: Advancements) {
				encoder.encodeString(value.advancements.joinToString(",", "{", "}") {
					json.encodeToJsonElement(Advancement.Companion.AdvancementSerializer, it).jsonPrimitive.content
				})
			}
		}
	}
}

class AdvancementBuilder {
	private val advancements = mutableSetOf<Advancement>()

	fun advancement(
		advancement: AdvancementArgument,
		done: Boolean = true,
		block: MutableMap<String, Boolean>.() -> Unit = {}
	) {
		val criteria = buildMap(block)
		advancements.add(Advancement(advancement, done, criteria))
	}

	operator fun <T : AdvancementArgument> T.invoke(
		done: Boolean = true,
		block: MutableMap<String, Boolean>.() -> Unit = {}
	) {
		val criteria = buildMap(block)
		advancements.add(Advancement(this, done, criteria))
	}

	fun build() = Advancements(advancements)
}
