package io.github.ayfri.kore.features.advancements

import io.github.ayfri.kore.features.advancements.AdvancementTriggerConditionSerialized.Companion.AdvancementTriggerConditionSerializedSerializer
import io.github.ayfri.kore.features.advancements.triggers.AdvancementTriggerCondition
import io.github.ayfri.kore.utils.snakeCase
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonTransformingSerializer
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.serializer

@Serializable
private data class AdvancementTriggerConditionSerialized<T : AdvancementTriggerCondition>(
	val conditions: EntityOrPredicates? = null,
	val condition: T,
) {
	companion object {
		class AdvancementTriggerConditionSerializedSerializer<T : AdvancementTriggerCondition>(
			serializer: KSerializer<T>,
		) : JsonTransformingSerializer<AdvancementTriggerConditionSerialized<T>>(Companion.serializer(serializer)) {
			override fun transformSerialize(element: JsonElement) = buildJsonObject {
				element.jsonObject.forEach { (topKey, topValue) ->
					when (topKey) {
						"conditions" -> put("player", topValue)
						else -> topValue.jsonObject.forEach { (key, value) ->
							put(key, value)
						}
					}
				}
			}
		}
	}
}

@Serializable(with = AdvancementCriterion.Serializer::class)
data class AdvancementCriterion(
	val trigger: AdvancementTriggerCondition,
	var conditions: EntityOrPredicates? = null,
) {
	object Serializer : KSerializer<AdvancementCriterion> {
		override val descriptor = buildClassSerialDescriptor("AdvancementCriterion") {
			element<String>("trigger")
			element<AdvancementTriggerConditionSerialized<AdvancementTriggerCondition>>("conditions")
		}

		override fun deserialize(decoder: Decoder) = serializer<AdvancementCriterion>().deserialize(decoder)

		@OptIn(InternalSerializationApi::class)
		override fun serialize(encoder: Encoder, value: AdvancementCriterion) {
			encoder.encodeStructure(descriptor) {
				encodeStringElement(descriptor, 0, "minecraft:${value.trigger::class.simpleName!!.snakeCase()}")
				encodeSerializableElement(
					descriptor,
					1,
					AdvancementTriggerConditionSerializedSerializer(value.trigger::class.serializer()) as KSerializer<AdvancementTriggerConditionSerialized<*>>,
					AdvancementTriggerConditionSerialized(value.conditions, value.trigger)
				)
			}
		}
	}
}
