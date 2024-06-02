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
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

@Serializable(SelectorArguments.Companion.SelectorArgumentsSerializer::class)
data class SelectorArguments(
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
	var scores: Scores<SelectorScore>? = null,
	var sort: Sort? = null,
	@SerialName("gamemode")
	private var _gamemodes: MutableList<GamemodeOption> = mutableListOf(),
	@SerialName("name")
	private var _names: MutableList<StringOption> = mutableListOf(),
	@SerialName("nbt")
	private var _nbt: MutableList<NbtCompoundOption> = mutableListOf(),
	@SerialName("predicate")
	private var _predicates: MutableList<PredicateOption> = mutableListOf(),
	@SerialName("tag")
	private var _tags: MutableList<StringOption> = mutableListOf(),
	@SerialName("team")
	private var _teams: MutableList<StringOption> = mutableListOf(),
	@SerialName("type")
	private var _types: MutableList<EntityTypeOption> = mutableListOf(),
) {
	@Transient
	var gamemode
		get() = (_gamemodes.firstOrNull() ?: GamemodeOption()).value
		set(value) {
			if (_gamemodes.lastOrNull() != null && _gamemodes.lastOrNull()?.value == null) _gamemodes.last().value = value
			else _gamemodes += GamemodeOption(value)
		}

	@Transient
	var name
		get() = (_names.firstOrNull() ?: StringOption()).value
		set(value) {
			if (_names.lastOrNull() != null && _names.lastOrNull()?.value == null) _names.last().value = value
			else _names += StringOption(value)
		}

	@Transient
	var nbt
		get() = (_nbt.firstOrNull() ?: NbtCompoundOption()).value
		set(value) {
			if (_nbt.lastOrNull() != null && _nbt.lastOrNull()?.value == null) _nbt.last().value = value
			else _nbt += NbtCompoundOption(value)
		}

	@Transient
	var predicate
		get() = (_predicates.firstOrNull() ?: PredicateOption()).value
		set(value) {
			if (_predicates.lastOrNull() != null && _predicates.lastOrNull()?.value == null) _predicates.last().value = value
			else _predicates += PredicateOption(value)
		}

	@Transient
	var tag
		get() = (_tags.firstOrNull() ?: StringOption()).value
		set(value) {
			if (_tags.lastOrNull() != null && _tags.lastOrNull()?.value == null) _tags.last().value = value
			else _tags += StringOption(value)
		}

	@Transient
	var team
		get() = (_teams.firstOrNull() ?: StringOption()).value
		set(value) {
			if (_teams.lastOrNull() != null && _teams.lastOrNull()?.value == null) _teams.last().value = value
			else _teams += StringOption(value)
		}

	@Transient
	var type
		get() = (_types.firstOrNull() ?: EntityTypeOption()).value
		set(value) {
			if (_types.lastOrNull() != null && _types.lastOrNull()?.value == null) _types.last().value = value
			else _types += EntityTypeOption(value)
		}

	operator fun Gamemode.not() = apply { _gamemodes += GamemodeOption(null, true) }
	operator fun NbtCompound.not() = apply { _nbt += NbtCompoundOption(null, true) }
	operator fun PredicateArgument.not() = apply { _predicates += PredicateOption(null, true) }
	operator fun EntityTypeArgument.not() = apply { _types += EntityTypeOption(null, true) }

	operator fun String.not() = "!$this"

	fun copyFrom(other: SelectorArguments) {
		advancements = other.advancements
		distance = other.distance
		dx = other.dx
		dy = other.dy
		dz = other.dz
		level = other.level
		limit = other.limit
		scores = other.scores
		sort = other.sort
		x = other.x
		xRotation = other.xRotation
		y = other.y
		yRotation = other.yRotation
		z = other.z
		_gamemodes = other._gamemodes
		_names = other._names
		_nbt = other._nbt
		_predicates = other._predicates
		_tags = other._tags
		_teams = other._teams
		_types = other._types
	}

	companion object {
		object SelectorArgumentsSerializer : KSerializer<SelectorArguments> {
			override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("SelectorNbtData", PrimitiveKind.STRING)

			override fun deserialize(decoder: Decoder) = SelectorArguments()

			override fun serialize(encoder: Encoder, value: SelectorArguments) {
				val argumentsMap = mutableMapOf<String, Any?>()
				value::class.memberProperties.forEach {
					it.isAccessible = true
					if (it.hasAnnotation<Transient>()) return@forEach

					val serialName = it.findAnnotation<SerialName>()?.value ?: it.name
					argumentsMap[serialName] = it.getter.call(value)
				}

				encoder.encodeString(argumentsMap.entries.filter { it.value != null }.sortedBy { it.key }.mapNotNull { (key, value) ->
					when (value) {
						is SelectorAdvancements -> "$key=${json.encodeToJsonElement(value).jsonPrimitive.content}"

						is List<*> -> when {
							value.isEmpty() -> return@mapNotNull null
							else -> value.joinToString(",") { "$key=$it" }
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
