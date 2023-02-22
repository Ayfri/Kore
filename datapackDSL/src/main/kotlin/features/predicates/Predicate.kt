package features.predicates

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import utils.snakeCase

@Serializable
private data class PredicateSerialized(val condition: String, val predicate: Predicate) {
	companion object {
		object PredicateSerializedSerializer : JsonTransformingSerializer<PredicateSerialized>(serializer()) {
			override fun transformSerialize(element: JsonElement) = buildJsonObject {
				val condition = element.jsonObject["condition"]!!
				put("condition", condition)
				if (condition.jsonPrimitive.content == "minecraft:impossible") return@buildJsonObject

				element.jsonObject["predicate"]?.let { predicate ->
					predicate.jsonObject.forEach { (key, value) ->
						if (key != "type") put(key, value)
					}
				}
			}
		}
	}
}

@Serializable
internal data class PredicateFile(val predicates: Collection<Predicate> = mutableListOf()) {
	companion object {
		object PredicateFileSerializer : KSerializer<PredicateFile> by serializer() {
			override val descriptor = serialDescriptor<PredicateSerialized>()

			override fun serialize(encoder: Encoder, value: PredicateFile) {
				encoder.encodeSerializableValue(
					ListSerializer(PredicateSerialized.Companion.PredicateSerializedSerializer),
					value.predicates.map { PredicateSerialized("minecraft:${it::class.simpleName!!.snakeCase()}", it) }
				)
			}
		}
	}
}

@Serializable
sealed interface Predicate

internal fun Collection<Predicate>.toPredicateFile() = PredicateFile(this)
