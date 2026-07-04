package io.github.ayfri.kore.arguments.selector

import io.github.ayfri.kore.arguments.Advancement
import io.github.ayfri.kore.arguments.enums.Gamemode
import io.github.ayfri.kore.arguments.numbers.ranges.*
import io.github.ayfri.kore.arguments.scores.Scores
import io.github.ayfri.kore.arguments.scores.SelectorScore
import io.github.ayfri.kore.generated.arguments.types.AdvancementArgument
import io.github.ayfri.kore.generated.arguments.types.EntityTypeArgument
import io.github.ayfri.kore.generated.arguments.types.PredicateArgument
import io.github.ayfri.kore.serializers.ToStringSerializer
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.StringifiedNbt

/**
 * Container for target selector arguments used to build Minecraft target selectors (e.g. `@p[distance=..]`).
 * See: https://minecraft.wiki/w/Target_selectors
 */
@OptIn(ExperimentalSerializationApi::class)
@KeepGeneratedSerializer
@Serializable(SelectorArguments.Companion.SelectorArgumentsSerializer::class)
data class SelectorArguments(
	/** X coordinate for this selector. */
	var x: Double? = null,
	/** Y coordinate for this selector. */
	var y: Double? = null,
	/** Z coordinate for this selector. */
	var z: Double? = null,
	/**
	 * X-axis delta for volume selection (dx).
	 *
	 * Selects entities whose hitboxes intersect the cuboid from (x, y, z) to (x + dx, y + dy, z + dz),
	 * with the farthest corner offset by (1, 1, 1). If only dx is set, dy and dz default to 0.
	 *
	 * Example: @e[x=1,y=2,z=3,dx=4] selects entities in (1,2,3) to (5,2,3).
	 */
	var dx: Double? = null,

	/**
	 * Y-axis delta for volume selection (dy).
	 *
	 * Selects entities whose hitboxes intersect the cuboid from (x, y, z) to (x + dx, y + dy, z + dz),
	 * with the farthest corner offset by (1, 1, 1). If only dy is set, dx and dz default to 0.
	 *
	 * Example: @e[x=1,y=2,z=3,dy=5] selects entities in (1,2,3) to (1,7,3).
	 */
	var dy: Double? = null,

	/**
	 * Z-axis delta for volume selection (dz).
	 *
	 * Selects entities whose hitboxes intersect the cuboid from (x, y, z) to (x + dx, y + dy, z + dz),
	 * with the farthest corner offset by (1, 1, 1). If only dz is set, dx and dy default to 0.
	 *
	 * Example: @e[x=1,y=2,z=3,dz=-6] selects entities in (1,2,-3) to (1,2,4).
	 */
	var dz: Double? = null,
	/** X rotation range for this selector. */
	@SerialName("x_rotation")
	var xRotation: FloatRangeOrFloat? = null,
	/** Y rotation range for this selector. */
	@SerialName("y_rotation")
	var yRotation: FloatRangeOrFloat? = null,
	/** Advancements filter for this selector. */
	var advancements: SelectorAdvancements? = null,
	/** Distance range for this selector. */
	var distance: Range? = null,
	/** Limit for this selector. */
	var limit: Int? = null,
	/** Level range for this selector. */
	var level: IntRangeOrInt? = null,
	/** Scores filter for this selector. */
	@Serializable(ScoresSerializer::class)
	var scores: Scores<SelectorScore>? = null,
	/** Sorting order for this selector. */
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
	/** Selected `Gamemode` for this selector (maps to the `gamemode` argument). */
	var gamemode
		get() = (_gamemodes.firstOrNull() ?: GamemodeOption()).value
		set(value) {
			if (_gamemodes.lastOrNull() != null && _gamemodes.lastOrNull()?.value == null) _gamemodes.last().value = value
			else _gamemodes += GamemodeOption(value)
		}

	@Transient
	/** Filter by entity/player name for this selector (maps to the `name` argument). */
	var name
		get() = (_names.firstOrNull() ?: StringOption()).value
		set(value) {
			if (_names.lastOrNull() != null && _names.lastOrNull()?.value == null) _names.last().value = value
			else _names += StringOption(value)
		}

	@Transient
	/** Filter by NBT data for this selector (maps to the `nbt` argument). */
	var nbt
		get() = (_nbt.firstOrNull() ?: NbtCompoundOption()).value
		set(value) {
			if (_nbt.lastOrNull() != null && _nbt.lastOrNull()?.value == null) _nbt.last().value = value
			else _nbt += NbtCompoundOption(value)
		}

	@Transient
	/** Filter by predicate for this selector (maps to the `predicate` argument). */
	var predicate
		get() = (_predicates.firstOrNull() ?: PredicateOption()).value
		set(value) {
			if (_predicates.lastOrNull() != null && _predicates.lastOrNull()?.value == null) _predicates.last().value = value
			else _predicates += PredicateOption(value)
		}

	@Transient
	/** Filter by tag for this selector (maps to the `tag` argument). */
	var tag
		get() = (_tags.firstOrNull() ?: StringOption()).value
		set(value) {
			if (_tags.lastOrNull() != null && _tags.lastOrNull()?.value == null) _tags.last().value = value
			else _tags += StringOption(value)
		}

	@Transient
	/** Filter by team name for this selector (maps to the `team` argument). */
	var team
		get() = (_teams.firstOrNull() ?: StringOption()).value
		set(value) {
			if (_teams.lastOrNull() != null && _teams.lastOrNull()?.value == null) _teams.last().value = value
			else _teams += StringOption(value)
		}

	@Transient
	/** Filter by entity type for this selector (maps to the `type` argument). */
	var type
		get() = (_types.firstOrNull() ?: EntityTypeOption()).value
		set(value) {
			if (_types.lastOrNull() != null && _types.lastOrNull()?.value == null) _types.last().value = value
			else _types += EntityTypeOption(value)
		}

	/** Invert the next gamemode option (e.g. `!survival`). */
	operator fun Gamemode.not() = apply { _gamemodes += GamemodeOption(invert = true) }
	/** Invert the next NBT option. */
	operator fun NbtCompound.not() = apply { _nbt += NbtCompoundOption(invert = true) }
	/** Invert the next predicate option. */
	operator fun PredicateArgument.not() = apply { _predicates += PredicateOption(invert = true) }
	/** Invert the next entity type option. */
	operator fun EntityTypeArgument.not() = apply { _types += EntityTypeOption(invert = true) }

	/** Prefix a string with '!' to invert string-based options. */
	operator fun String.not() = "!$this"

	/** Copy all selector argument values from another instance. */
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
		_gamemodes = other._gamemodes.toMutableList()
		_names = other._names.toMutableList()
		_nbt = other._nbt.toMutableList()
		_predicates = other._predicates.toMutableList()
		_tags = other._tags.toMutableList()
		_teams = other._teams.toMutableList()
		_types = other._types.toMutableList()
	}

	companion object {
		/**
		 * Parses selector arguments from their command representation, the content between `[` and `]`
		 * (e.g. `limit=1,tag=!foo,scores={foo=1..}`).
		 */
		fun fromString(input: String): SelectorArguments {
			val arguments = SelectorArguments()
			splitSelectorEntries(input).filter { it.isNotEmpty() }.forEach { entry ->
				val key = entry.substringBefore('=')
				val raw = entry.substringAfter('=')
				val inverted = raw.startsWith("!")
				val value = raw.removePrefix("!")
				when (key) {
					"advancements" -> arguments.advancements = parseAdvancements(value)
					"distance" -> arguments.distance = parseRange(value)
					"dx" -> arguments.dx = value.toDouble()
					"dy" -> arguments.dy = value.toDouble()
					"dz" -> arguments.dz = value.toDouble()
					"gamemode" -> arguments._gamemodes +=
						GamemodeOption(value.ifEmpty { null }?.let { Gamemode.valueOf(it.uppercase()) }, inverted)

					"level" -> arguments.level = parseIntRangeOrInt(value)
					"limit" -> arguments.limit = value.toInt()
					"name" -> arguments._names += StringOption(value, inverted)
					"nbt" -> arguments._nbt +=
						NbtCompoundOption(value.ifEmpty { null }
							?.let { StringifiedNbt.decodeFromString<NbtCompound>(it) }, inverted)

					"predicate" -> arguments._predicates +=
						PredicateOption(value.ifEmpty { null }
							?.let { parseResourceLocation(it, PredicateArgument::invoke) }, inverted)

					"scores" -> arguments.scores = Scores(
						splitSelectorEntries(value.removeSurrounding("{", "}")).filter { it.isNotEmpty() }
							.toMutableSet()
					)

					"sort" -> arguments.sort = Sort.valueOf(value.uppercase())
					"tag" -> arguments._tags += StringOption(value, inverted)
					"team" -> arguments._teams += StringOption(value, inverted)
					"type" -> arguments._types +=
						EntityTypeOption(value.ifEmpty { null }
							?.let { parseResourceLocation(it, EntityTypeArgument::invoke) }, inverted)

					"x" -> arguments.x = value.toDouble()
					"x_rotation" -> arguments.xRotation = parseFloatRangeOrFloat(value)
					"y" -> arguments.y = value.toDouble()
					"y_rotation" -> arguments.yRotation = parseFloatRangeOrFloat(value)
					"z" -> arguments.z = value.toDouble()
					else -> error("Unknown selector argument: '$key'")
				}
			}
			return arguments
		}

		data object ScoresSerializer : ToStringSerializer<Scores<SelectorScore>>({ "{${scores.joinToString(",")}}" })

		data object SelectorArgumentsSerializer : KSerializer<SelectorArguments> {
			override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("SelectorNbtData", PrimitiveKind.STRING)

			override fun deserialize(decoder: Decoder) = fromString(decoder.decodeString())

			override fun serialize(encoder: Encoder, value: SelectorArguments) {
				val arguments = json.encodeToJsonElement(generatedSerializer(), value).jsonObject
				encoder.encodeString(arguments.entries.sortedBy { it.key }.flatMap { (key, element) ->
					when (element) {
						is JsonArray -> element.map { "$key=${it.jsonPrimitive.content}" }
						else -> listOf("$key=${element.jsonPrimitive.content}")
					}
				}.joinToString(","))
			}
		}
	}
}

/** Splits selector entries on top-level commas, ignoring commas inside braces, brackets and quoted strings. */
private fun splitSelectorEntries(input: String): List<String> {
	val entries = mutableListOf<String>()
	val current = StringBuilder()
	var depth = 0
	var quote: Char? = null
	var index = 0
	while (index < input.length) {
		val char = input[index]
		when {
			quote != null -> {
				current.append(char)
				when (char) {
					'\\' -> {
						index++
						if (index < input.length) current.append(input[index])
					}

					quote -> quote = null
				}
			}

			char == '"' || char == '\'' -> {
				quote = char
				current.append(char)
			}

			char == '{' || char == '[' -> {
				depth++
				current.append(char)
			}

			char == '}' || char == ']' -> {
				depth--
				current.append(char)
			}

			char == ',' && depth == 0 -> {
				entries += current.toString()
				current.clear()
			}

			else -> current.append(char)
		}
		index++
	}
	if (current.isNotEmpty()) entries += current.toString()
	return entries
}

private fun <T> parseResourceLocation(id: String, factory: (String, String) -> T) =
	factory(id.substringAfter(':'), id.substringBefore(':', "minecraft"))

private fun parseIntRangeOrInt(value: String) = when {
	".." in value -> IntRangeOrInt(
		IntRange(
			value.substringBefore("..").toIntOrNull(),
			value.substringAfter("..").toIntOrNull()
		)
	)

	else -> IntRangeOrInt(int = value.toInt())
}

private fun parseFloatRangeOrFloat(value: String) = when {
	".." in value -> FloatRangeOrFloat(
		FloatRange(
			value.substringBefore("..").toDoubleOrNull(),
			value.substringAfter("..").toDoubleOrNull()
		)
	)

	else -> FloatRangeOrFloat(double = value.toDouble())
}

private fun parseRange(value: String): Range = when {
	'.' in value.replace("..", "") -> parseFloatRangeOrFloat(value)
	else -> parseIntRangeOrInt(value)
}

private fun parseAdvancements(value: String) = SelectorAdvancements(
	splitSelectorEntries(value.removeSurrounding("{", "}")).filter { it.isNotEmpty() }.map { entry ->
		val id = entry.substringBefore('=')
		val state = entry.substringAfter('=')
		val argument = parseResourceLocation(id, AdvancementArgument::invoke)
		when {
			state.startsWith("{") -> Advancement(
				argument,
				criteria = splitSelectorEntries(state.removeSurrounding("{", "}"))
					.filter { it.isNotEmpty() }
					.associate { it.substringBefore('=') to it.substringAfter('=').toBooleanStrict() },
			)

			else -> Advancement(argument, done = state.toBooleanStrict())
		}
	}.toSet()
)
