package features.predicates.conditions

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.listSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import serializers.DirectPolymorphicSerializer
import utils.snakeCase

typealias PredicateCondition = @Serializable(PredicateConditionSerializer::class) PredicateConditionSurrogate

typealias PredicateConditions = @Serializable(PredicateConditionsSerializer::class) List<PredicateCondition>

//typealias PredicateConditions = @Serializable List<PredicateCondition>

object PredicateConditionsSerializer : KSerializer<List<PredicateCondition>?> {
	@OptIn(ExperimentalSerializationApi::class)
	override val descriptor = listSerialDescriptor(PredicateConditionSerializer.descriptor)
	override fun deserialize(decoder: Decoder) = error("PredicateConditions cannot be deserialized")

	override fun serialize(encoder: Encoder, value: List<PredicateCondition>?) {
		/*require(encoder is JsonEncoder) { "Predicate can only be serialized with Json" }

		val element = buildJsonArray {
			value.forEach { condition ->
				add(Json.encodeToJsonElement(PredicateConditionSerializer, condition))
			}
		}

		encoder.encodeJsonElement(element)*/

		if (value != null)
			encoder.encodeSerializableValue(ListSerializer(PredicateConditionSurrogate.Companion.PredicateConditionSerializer), value)
	}
}
/*

object PredicateConditionsSerializer :
	KSerializer<List<PredicateCondition>> by ListSerializer(PredicateConditionSerializer) {
	override fun serialize(encoder: Encoder, value: List<PredicateCondition>) {
		require(encoder is JsonEncoder) { "Predicate can only be serialized with Json" }
	}
}
*/



@Serializable
sealed interface PredicateConditionSurrogate {
	companion object {
		object PredicateConditionSerializer : KSerializer<PredicateConditionSurrogate> by serializer() {
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

object PredicateConditionSerializer :
	KSerializer<PredicateCondition> by DirectPolymorphicSerializer(PredicateConditionSurrogate.serializer())
