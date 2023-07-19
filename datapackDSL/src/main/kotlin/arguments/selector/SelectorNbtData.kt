package arguments.selector

import arguments.enums.Gamemode
import arguments.numbers.FloatRangeOrFloat
import arguments.numbers.IntRangeOrInt
import arguments.numbers.Range
import arguments.scores.Scores
import arguments.scores.SelectorScore
import arguments.types.resources.EntityTypeArgument
import arguments.types.resources.PredicateArgument
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

sealed interface Invertable<T> {
	var value: T?
	var invert: Boolean
}

@Serializable(GamemodeSelector.Companion.GamemodeSelectorSerializer::class)
data class GamemodeSelector(override var value: Gamemode? = null, override var invert: Boolean = false) : Invertable<Gamemode> {
	override fun toString() = when {
		value == null -> ""
		invert -> "!${json.encodeToJsonElement(value).jsonPrimitive.content}"
		else -> json.encodeToJsonElement(value).jsonPrimitive.content
	}

	companion object {
		data object GamemodeSelectorSerializer : ToStringSerializer<GamemodeSelector>()
	}
}

@Serializable(NbtCompoundSelector.Companion.NbtDataSelectorSerializer::class)
data class NbtCompoundSelector(override var value: NbtCompound? = null, override var invert: Boolean = false) :
	Invertable<NbtCompound> {
	override fun toString() = when {
		value == null -> ""
		invert -> "!${StringifiedNbt.encodeToString(value)}"
		else -> StringifiedNbt.encodeToString(value)
	}

	companion object {
		data object NbtDataSelectorSerializer : ToStringSerializer<NbtCompoundSelector>()
	}
}

@Serializable(EntityTypeSelector.Companion.EntityTypeSelectorSerializer::class)
data class EntityTypeSelector(override var value: EntityTypeArgument? = null, override var invert: Boolean = false) :
	Invertable<EntityTypeArgument> {
	override fun toString() = when {
		value == null -> ""
		invert -> "!${json.encodeToJsonElement(value).jsonPrimitive.content}"
		else -> json.encodeToJsonElement(value).jsonPrimitive.content
	}

	companion object {
		data object EntityTypeSelectorSerializer : ToStringSerializer<EntityTypeSelector>()
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
	var predicate: PredicateArgument? = null,
	var scores: Scores<SelectorScore>? = null,
	var sort: Sort? = null,
	var tag: String? = null,
	var team: String? = null,
	@SerialName("type")
	private var _type: EntityTypeSelector = EntityTypeSelector(),
	@SerialName("gamemode")
	private var _gamemode: GamemodeSelector = GamemodeSelector(),
	@SerialName("nbt")
	private var _nbt: NbtCompoundSelector = NbtCompoundSelector(),
) {
	@Transient
	var gamemode by _gamemode::value

	@Transient
	var nbt by _nbt::value

	@Transient
	var type by _type::value

	fun advancements(block: AdvancementBuilder.() -> Unit) {
		val builder = AdvancementBuilder()
		builder.block()
		advancements = builder.build()
	}

	operator fun Gamemode.not() = apply { _gamemode.invert = true }
	operator fun NbtCompound.not() = apply { _nbt.invert = true }
	operator fun EntityTypeArgument.not() = apply { _type.invert = true }

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

						is Invertable<*> -> when (value.value) {
							null -> return@mapNotNull null
							else -> "$key=$value"
						}

						is PredicateArgument -> "$key=${json.encodeToJsonElement(value).jsonPrimitive.content}"
						is EntityTypeArgument -> "$key=${json.encodeToJsonElement(value).jsonPrimitive.content}"

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
