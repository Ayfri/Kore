package io.github.ayfri.kore.arguments.selector

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.enums.Gamemode
import io.github.ayfri.kore.arguments.numbers.ranges.FloatRangeOrFloat
import io.github.ayfri.kore.arguments.numbers.ranges.IntRangeOrInt
import io.github.ayfri.kore.arguments.numbers.ranges.Range
import io.github.ayfri.kore.arguments.scores.Scores
import io.github.ayfri.kore.arguments.scores.SelectorScore
import io.github.ayfri.kore.arguments.types.resources.EntityTypeArgument
import io.github.ayfri.kore.arguments.types.resources.PredicateArgument
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.NbtTag
import net.benwoodworth.knbt.StringifiedNbt
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonPrimitive

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
	@Contextual var advancements: SelectorAdvancements? = null,
	var distance: Range? = null,
	var limit: Int? = null,
	var level: IntRangeOrInt? = null,
	var name: String? = null,
	var scores: Scores<SelectorScore>? = null,
	var sort: Sort? = null,
	var tag: String? = null,
	var team: String? = null,
	@SerialName("gamemode")
	private var _gamemode: GamemodeOption = GamemodeOption(),
	@SerialName("nbt")
	private var _nbt: NbtCompoundOption = NbtCompoundOption(),
	@SerialName("predicate")
	private var _predicate: PredicateOption = PredicateOption(),
	@SerialName("type")
	private var _type: EntityTypeOption = EntityTypeOption(),
) {
	@Transient
	var gamemode by _gamemode::value

	@Transient
	var nbt by _nbt::value

	@Transient
	var predicate by _predicate::value

	@Transient
	var type by _type::value

	fun advancements(block: AdvancementBuilder.() -> Unit) {
		advancements = AdvancementBuilder().apply(block).build()
	}

	operator fun Gamemode.not() = apply { _gamemode.invert = true }
	operator fun NbtCompound.not() = apply { _nbt.invert = true }
	operator fun PredicateArgument.not() = apply { _predicate.invert = true }
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
		tag = other.tag
		advancements = other.advancements
		scores = other.scores
		sort = other.sort
		_gamemode = other._gamemode
		_nbt = other._nbt
		_predicate = other._predicate
		_type = other._type
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
						is SelectorAdvancements -> "$key=${json.encodeToJsonElement(value).jsonPrimitive.content}"

						is InvertableOption<*> -> when (value.value) {
							null -> return@mapNotNull null
							else -> "$key=$value"
						}

						is Argument -> "$key=${json.encodeToJsonElement(value).jsonPrimitive.content}"

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
