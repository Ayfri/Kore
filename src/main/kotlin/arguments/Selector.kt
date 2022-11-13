package arguments

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonPrimitive
import nbt.NbtData
import serializers.LowercaseSerializer
import serializers.ToStringSerializer
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

@OptIn(ExperimentalSerializationApi::class)
internal val json = Json {
	ignoreUnknownKeys = true
	allowStructuredMapKeys = true
	explicitNulls = false
}

enum class SelectorType(val value: String) {
	NEAREST_PLAYER("p"),
	RANDOM_PLAYER("r"),
	ALL_PLAYERS("a"),
	ALL_ENTITIES("e"),
	SELF("s"),
}

@Serializable(Sort.Companion.SortSerializer::class)
enum class Sort {
	NEAREST,
	FURTHEST,
	RANDOM,
	ARBITRARY;
	
	companion object {
		val values = Sort.values()
		
		object SortSerializer : LowercaseSerializer<Sort>(values)
	}
}

@Serializable(GamemodeSelector.Companion.GamemodeSelectorSerializer::class)
class GamemodeSelector(var gamemode: Gamemode? = null, var invert: Boolean = false) {
	override fun toString() = when {
		gamemode == null -> ""
		invert -> "!${json.encodeToJsonElement(gamemode).jsonPrimitive.content}"
		else -> json.encodeToJsonElement(gamemode).jsonPrimitive.content
	}
	
	companion object {
		object GamemodeSelectorSerializer : ToStringSerializer<GamemodeSelector>()
	}
}

@Serializable(SelectorNbtData.Companion.SelectorNbtDataSerializer::class)
data class SelectorNbtData(
	var x: Double? = null,
	var y: Double? = null,
	var z: Double? = null,
	var dx: Double? = null,
	var dy: Double? = null,
	var dz: Double? = null,
	@SerialName("x_rotation")
	var xRotation: Double? = null,
	@SerialName("y_rotation")
	var yRotation: Double? = null,
	var distance: Double? = null,
	var limit: Int? = null,
	var level: Range? = null,
	var team: String? = null,
	var name: String? = null,
	var type: String? = null,
	var tag: String? = null,
	var nbtData: NbtData? = null,
	var advancements: Advancements? = null,
	var scores: Scores? = null,
	var sort: Sort? = null,
) {
	@SerialName("gamemode")
	private var _gamemode: GamemodeSelector = GamemodeSelector()
	
	@Transient
	var gamemode
		get() = _gamemode.gamemode
		set(value) {
			_gamemode.gamemode = value
		}
	
	operator fun Gamemode.not(): Gamemode {
		_gamemode.invert = true
		return this
	}
	
	operator fun String.not() = "!$this"
	
	companion object {
		object SelectorNbtDataSerializer : KSerializer<SelectorNbtData> {
			override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("SelectorNbtData", PrimitiveKind.STRING)
			
			override fun deserialize(decoder: Decoder) = SelectorNbtData()
			
			override fun serialize(encoder: Encoder, value: SelectorNbtData) {
				val map = mutableMapOf<String, Any?>()
				value::class.memberProperties.forEach {
					it.isAccessible = true
					if (it.hasAnnotation<Transient>()) return@forEach
					
					val serialName = it.findAnnotation<SerialName>()?.value ?: it.name
					map[serialName] = it.getter.call(value)
				}
				
				encoder.encodeString(map.filter { it.value != null }.map { (key, value) ->
					when (value) {
						is GamemodeSelector -> "$key=${json.encodeToJsonElement(value).jsonPrimitive.content}"
						is Sort -> "$key=${json.encodeToJsonElement(value).jsonPrimitive.content}"
						is Scores -> "$key=${json.encodeToJsonElement(value).jsonPrimitive.content}"
						is Advancements -> "$key=${json.encodeToJsonElement(value).jsonPrimitive.content}"
						is List<*> -> "$key=${json.encodeToJsonElement(value).jsonPrimitive.content}"
						is Map<*, *> -> "$key=${json.encodeToJsonElement(value).jsonPrimitive.content}"
						else -> "$key=$value"
					}
				}.joinToString(","))
			}
		}
	}
}

/**
 * Unescape a string.
 * Traverse the string and replace escaped characters with their unescaped version.
 * If the string was inside a double quote, the returned string will be without the double quotes.
 * @receiver The string to unescape.
 * @return The unescaped string.
 */
private fun String.unescape(): String {
	var result = this
	for (i in 0 until result.length - 2) {
		if (result[i] == '\\') {
			result = result.replaceRange(i, i + 2, result[i + 1].toString())
		}
	}
	
	return when {
		result.startsWith('"') && result.endsWith('"') -> result.substring(1, result.length - 1)
		else -> result
	}
}

data class Selector(val base: SelectorType) {
	val nbtData = SelectorNbtData()
	
	override fun toString(): String {
		val builder = StringBuilder("@")
		builder.append(base.value)
		if (nbtData != SelectorNbtData()) {
			builder.append("[")
			builder.append(json.encodeToJsonElement(nbtData).toString().unescape())
			builder.append("]")
		}
		
		return builder.toString()
	}
}
