package tags

import DataPack
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonTransformingSerializer
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.serializer
import java.io.File

@Serializable
data class TagEntry(
	@SerialName("id") val name: String,
	val required: Boolean? = null,
) {
	companion object {
		/**
		 * This is a custom serializer for TagEntry because the required field is optional.
		 * If it is not present, it should output a string, but if it is present, it should output an object.
		 */
		object Serializer : JsonTransformingSerializer<TagEntry>(serializer()) {
			override fun transformSerialize(element: JsonElement) = when {
				element.jsonObject["required"] == null -> element.jsonObject["id"]!!
				else -> element
			}
		}
	}
}

@Serializable(SerializedTag.Companion.Serializer::class)
data class SerializedTag(
	val replace: Boolean = false,
	val values: List<TagEntry>,
) {
	companion object {
		object Serializer : KSerializer<SerializedTag> {
			override val descriptor = buildClassSerialDescriptor("SerializedTag") {
				element<Boolean>("replace")
				element<List<TagEntry>>("values")
			}
			
			override fun deserialize(decoder: Decoder) = serializer<SerializedTag>().deserialize(decoder)
			
			override fun serialize(encoder: Encoder, value: SerializedTag) {
				encoder.encodeStructure(descriptor) {
					encodeBooleanElement(descriptor, 0, value.replace)
					encodeSerializableElement(descriptor, 1, ListSerializer(TagEntry.Companion.Serializer), value.values)
				}
			}
		}
	}
}

data class Tag(
	val name: String,
	val replace: Boolean = false,
	val values: MutableList<TagEntry> = mutableListOf(),
) {
	operator fun plusAssign(value: TagEntry) {
		values += value
	}
	
	operator fun plusAssign(value: String) {
		values += TagEntry(value)
	}
	
	operator fun plusAssign(value: Pair<String, Boolean>) {
		values += TagEntry(value.first, value.second)
	}
	
	fun addTag(name: String, required: Boolean? = null) {
		values += TagEntry("#$name", required)
	}
	
	fun add(name: String, namespace: String = "minecraft", group: Boolean = false, required: Boolean? = null) {
		values += TagEntry("${if (group) "#" else ""}$namespace:$name", required)
	}
	
	fun generate(tagsDir: File) {
		val file = File(tagsDir, "$name.json")
		file.parentFile.mkdirs()
		file.writeText(DataPack.jsonEncoder.encodeToString(SerializedTag(replace, values)))
	}
}
