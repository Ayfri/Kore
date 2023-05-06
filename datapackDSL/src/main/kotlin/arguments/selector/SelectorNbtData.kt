package arguments.selector

import arguments.Argument
import arguments.enums.Gamemode
import arguments.numbers.FloatRangeOrFloat
import arguments.numbers.IntRangeOrInt
import arguments.numbers.Range
import arguments.scores.Scores
import arguments.scores.SelectorScore
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonPrimitive
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.NbtTag
import net.benwoodworth.knbt.StringifiedNbt
import serializers.ToStringSerializer
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

@Serializable(GamemodeSelector.Companion.GamemodeSelectorSerializer::class)
data class GamemodeSelector(var gamemode: Gamemode? = null, var invert: Boolean = false) {
	override fun toString() = when {
		gamemode == null -> ""
		invert -> "!${json.encodeToJsonElement(gamemode).jsonPrimitive.content}"
		else -> json.encodeToJsonElement(gamemode).jsonPrimitive.content
	}

	companion object {
		object GamemodeSelectorSerializer : ToStringSerializer<GamemodeSelector>()
	}
}

@Serializable(NbtCompoundSelector.Companion.NbtDataSelectorSerializer::class)
data class NbtCompoundSelector(var nbt: NbtCompound? = null, var invert: Boolean = false) {
	override fun toString() = when {
		nbt == null -> ""
		invert -> "!${StringifiedNbt.encodeToString(nbt)}"
		else -> StringifiedNbt.encodeToString(nbt)
	}

	companion object {
		object NbtDataSelectorSerializer : ToStringSerializer<NbtCompoundSelector>()
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
	var xRotation: FloatRangeOrFloat? = null,
	@SerialName("y_rotation")
	var yRotation: FloatRangeOrFloat? = null,
	@Contextual var advancements: Advancements? = null,
	var distance: Range? = null,
	var limit: Int? = null,
	var level: IntRangeOrInt? = null,
	var name: String? = null,
	var predicate: Argument.Predicate? = null,
	var scores: Scores<SelectorScore>? = null,
	var sort: Sort? = null,
	var tag: String? = null,
	var team: String? = null,
	var type: String? = null,
	@SerialName("gamemode")
	@Serializable
	private var _gamemode: GamemodeSelector = GamemodeSelector(),
	@SerialName("nbt")
	@Serializable
	private var _nbt: NbtCompoundSelector = NbtCompoundSelector(),
) {
	@Transient
	var gamemode
		get() = _gamemode.gamemode
		set(value) {
			_gamemode.gamemode = value
		}

	@Transient
	var nbt
		get() = _nbt.nbt
		set(value) {
			_nbt.nbt = value
		}

	fun advancements(block: AdvancementBuilder.() -> Unit) {
		val builder = AdvancementBuilder()
		builder.block()
		advancements = builder.build()
	}

	operator fun Gamemode.not(): Gamemode {
		_gamemode.invert = true
		return this
	}

	operator fun NbtCompound.not(): NbtCompound {
		_nbt.invert = true
		return this
	}

	operator fun String.not() = "!$this"

	fun copyFrom(other: SelectorNbtData) {
		x = other.x
		y = other.y
		z = other.z
		dx = other.dx
		dy = other.dy
		dz = other.dz
		xRotation = other.xRotation
		yRotation = other.yRotation
		distance = other.distance
		limit = other.limit
		level = other.level
		team = other.team
		name = other.name
		type = other.type
		tag = other.tag
		advancements = other.advancements
		scores = other.scores
		sort = other.sort
		predicate = other.predicate
		_gamemode = other._gamemode
		_nbt = other._nbt
	}

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

				encoder.encodeString(map.filter { it.value != null }.mapNotNull { (key, value) ->
					when (value) {
						is Advancements -> "$key=${
							json.encodeToJsonElement(
								Advancements.Companion.AdvancementsSerializer,
								value
							).jsonPrimitive.content
						}"

						is GamemodeSelector -> when (value.gamemode) {
							null -> return@mapNotNull null
							else -> "$key=${json.encodeToJsonElement(value).jsonPrimitive.content}"
						}

						is NbtCompoundSelector -> when (value.nbt) {
							null -> return@mapNotNull null
							else -> "$key=$value"
						}

						is Argument.Predicate -> "$key=${json.encodeToJsonElement(value).jsonPrimitive.content}"

						is Scores<*> -> "$key={${value.scores.joinToString(",")}}"
						is Sort -> "$key=${json.encodeToJsonElement(value).jsonPrimitive.content}"
						is NbtTag -> "$key=${StringifiedNbt.encodeToString(value).removeSurrounding("\"")}"
						else -> "$key=$value"
					}
				}.joinToString(","))
			}
		}
	}
}
