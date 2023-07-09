package features.predicates.conditions

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.listSerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import utils.snakeCase

typealias PredicateCondition = @Serializable(PredicateConditionSurrogate.Companion.PredicateConditionSerializer::class) PredicateConditionSurrogate

typealias PredicateConditions = @Serializable(PredicateConditionsSerializer::class) List<PredicateCondition>

object PredicateConditionsSerializer : KSerializer<List<PredicateCondition>?> {
	@OptIn(ExperimentalSerializationApi::class)
	override val descriptor = listSerialDescriptor(PredicateConditionSurrogate.Companion.PredicateConditionSerializer.descriptor)
	override fun deserialize(decoder: Decoder) = error("PredicateConditions cannot be deserialized")

	override fun serialize(encoder: Encoder, value: List<PredicateCondition>?) {

		if (value != null)
			encoder.encodeSerializableValue(ListSerializer(PredicateConditionSurrogate.Companion.PredicateConditionSerializer), value)
	}
}


@Serializable
sealed interface PredicateConditionSurrogate {
	companion object {
		object PredicateConditionSerializer : KSerializer<PredicateConditionSurrogate> {
			override val descriptor = buildClassSerialDescriptor("PredicateCondition") {
				element("condition", serialDescriptor<String>())
			}

			override
			fun deserialize(decoder: Decoder) = error("PredicateCondition cannot be deserialized")

			override fun serialize(encoder: Encoder, value: PredicateConditionSurrogate) {
				require(encoder is JsonEncoder) { "Predicate can only be serialized with Json" }

				encoder.encodeJsonElement(buildJsonObject {
					val condition = "minecraft:${value::class.simpleName!!.snakeCase()}"
					put("condition", JsonPrimitive(condition))

					Json.encodeToJsonElement(value).jsonObject.forEach { (key, value) ->
						if (key != "type") put(key, value)
					}
				})
			}
		}
	}
}
