package features.predicates.conditions

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import utils.snakeCase

typealias PredicateCondition = @Serializable(PredicateConditionSerialized.Companion.PredicateConditionSerializer::class) PredicateConditionSerialized

val predicateConditionsSerializer = ListSerializer(PredicateConditionSerialized.Companion.PredicateConditionSerializer)

@Serializable
sealed interface PredicateConditionSerialized {
	companion object {

		@OptIn(ExperimentalSerializationApi::class)
		@Serializer(forClass = PredicateConditionSerialized::class)
		object PredicateConditionSerializer : KSerializer<PredicateConditionSerialized> by serializer() {
			override fun serialize(encoder: Encoder, value: PredicateConditionSerialized) {
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
