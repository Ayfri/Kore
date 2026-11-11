package io.github.ayfri.kore.website.components.doc

import androidx.compose.runtime.*
import io.github.ayfri.kore.website.components.common.CodeBlock
import io.github.ayfri.kore.website.utils.highlightCodeIn
import org.jetbrains.compose.web.dom.*

private const val SELECTOR_KOTLIN_ID = "selector-builder-kotlin"
private const val SELECTOR_TEXT_ID = "selector-builder-selector"

private val GAMEMODES = listOf("survival", "creative", "adventure", "spectator")
private val SORTS = listOf("nearest", "furthest", "random", "arbitrary")

/** The game rejects `type` on the player-only bases, and a limit means nothing on a base that picks a single target by itself. */
private enum class SelectorBase(val symbol: String, val call: String, val takesLimit: Boolean = false, val takesType: Boolean = false) {
	ALL_PLAYERS("@a", "allPlayers", takesLimit = true),
	ALL_ENTITIES("@e", "allEntities", takesLimit = true, takesType = true),
	NEAREST_ENTITY("@n", "nearestEntity", takesType = true),
	NEAREST_PLAYER("@p", "nearestPlayer"),
	RANDOM_PLAYER("@r", "randomPlayer", takesLimit = true),
	SELF("@s", "self", takesType = true);

	/** `@a` and `@e` have no order of their own: they take a sort, and `limitToOne`, Kore's short form of a limit of 1. */
	val selectsAll get() = this == ALL_PLAYERS || this == ALL_ENTITIES
}

private enum class MobType(val label: String, val id: String, val constant: String, val sprite: String) {
	ZOMBIE("zombie", "minecraft:zombie", "ZOMBIE", "item/zombie_spawn_egg"),
	SKELETON("skeleton", "minecraft:skeleton", "SKELETON", "item/skeleton_spawn_egg"),
	CREEPER("creeper", "minecraft:creeper", "CREEPER", "item/creeper_spawn_egg"),
	ARMOR_STAND("armor stand", "minecraft:armor_stand", "ARMOR_STAND", "item/armor_stand"),
}

/** On JS a round Double prints as `10`, the text Kore's `toStringTruncatedIfRound` writes: only the Kotlin literal needs its `.0` back. */
private fun Double.asLiteral() = toString().let { if ('.' in it) it else "$it.0" }

/**
 * The range argument [name] as the selector writes it and as its line of Kotlin, a null bound being open.
 * Whole bounds use Kore's Int ranges, the shorter calls: the selector text is the same either way.
 */
private fun range(name: String, min: Double?, max: Double?): Pair<String, String>? {
	val whole = listOfNotNull(min, max).all { it % 1 == 0.0 }
	fun literal(value: Double) = if (whole) value.toInt().toString() else value.asLiteral()
	val prefix = if (whole) "rangeOrInt" else "rangeOrDouble"
	val call = when {
		min == null -> "${prefix}End(${literal(max ?: return null)})"
		max == null -> "${prefix}Start(${literal(min)})"
		whole -> "rangeOrInt(${literal(min)}..${literal(max)})"
		else -> "rangeOrDouble(${literal(min)}, ${literal(max)})"
	}
	return "$name=${min ?: ""}..${max ?: ""}" to "$name = $call"
}

/** The text argument [name], typed as `foo` or as `!foo` to exclude the value, which Kore writes `!"foo"`. */
private fun text(name: String, typed: String) = typed.takeIf { it.isNotEmpty() }?.let {
	"$name=$it" to "$name = ${if (it.startsWith('!')) "!" else ""}${it.removePrefix("!").kotlin()}"
}

/** A slider whose 0 leaves the argument unset. */
@Composable
private fun OptionalSlider(id: String, label: String, value: Double?, max: Double, step: Double, onChange: (Double?) -> Unit) =
	SliderField("selector-builder-$id", "$label (0 for none)", value ?: 0.0, 0.0, max, step) { changed -> onChange(changed.takeIf { it > 0 }) }

/** Combines a base selector and a few arguments, showing the selector Kore writes and the Kotlin that writes it. */
@Composable
fun SelectorBuilder() {
	var base by remember { mutableStateOf(SelectorBase.ALL_ENTITIES) }
	var distanceMax by remember { mutableStateOf<Double?>(null) }
	var distanceMin by remember { mutableStateOf<Double?>(null) }
	var gamemode by remember { mutableStateOf<String?>(null) }
	var level by remember { mutableStateOf<Double?>(null) }
	var limit by remember { mutableStateOf<Int?>(null) }
	var name by remember { mutableStateOf("") }
	var sort by remember { mutableStateOf<String?>(null) }
	var tag by remember { mutableStateOf("") }
	var team by remember { mutableStateOf("") }
	var type by remember { mutableStateOf<MobType?>(null) }

	val parameters = if (limit == 1 && base.selectsAll) "(limitToOne = true)" else ""
	/** Each argument as written in the selector, in Kore's order, and its line of the Kotlin block when it has one. */
	val arguments = listOfNotNull(
		range("distance", distanceMin, distanceMax),
		gamemode?.let { "gamemode=$it" to "gamemode = Gamemode.${it.uppercase()}" },
		range("level", level, null),
		limit?.takeIf { base.takesLimit }?.let { "limit=$it" to "limit = $it".takeIf { parameters.isEmpty() } },
		text("name", name),
		sort?.takeIf { base.selectsAll }?.let { "sort=$it" to "sort = Sort.${it.uppercase()}" },
		text("tag", tag),
		text("team", team),
		type?.takeIf { base.takesType }?.let { "type=${it.id}" to "type = EntityTypes.${it.constant}" },
	)
	val lines = arguments.mapNotNull { it.second }
	val selector = base.symbol + if (arguments.isEmpty()) "" else arguments.joinToString(",", "[", "]") { it.first }
	val kotlin = base.call + if (lines.isEmpty()) parameters.ifEmpty { "()" } else lines.joinToString("\n\t", "$parameters {\n\t", "\n}")

	Div({ classes(DocControlsStyle.widget) }) {
		Div({ classes(DocControlsStyle.panel, DocControlsStyle.stack) }) {
			ControlField("base") {
				PillGroup("Base selector", SelectorBase.entries, base, SelectorBase::symbol) { base = it }
			}

			if (base.takesType) {
				ControlField("type") {
					Div({
						classes(DocControlsStyle.group)
						attr("aria-label", "Entity type")
						attr("role", "group")
					}) {
						MobType.entries.forEach { mob ->
							Pill(mob.label, mob == type, mob.sprite) { type = mob.takeUnless { it == type } }
						}
					}
				}
			}

			if (base.selectsAll) {
				ControlField("sort") {
					PillGroup("Sort", SORTS, sort, { it }) { chosen -> sort = chosen.takeUnless { it == sort } }
				}
			}

			ControlField("gamemode") {
				PillGroup("Gamemode", GAMEMODES, gamemode, { it }) { chosen -> gamemode = chosen.takeUnless { it == gamemode } }
			}

			Div({ classes(DocControlsStyle.grid) }) {
				OptionalSlider("distance-min", "min distance", distanceMin, 64.0, 0.5) { value ->
					distanceMin = value
					if (value != null) distanceMax = distanceMax?.coerceAtLeast(value)
				}
				OptionalSlider("distance-max", "max distance", distanceMax, 64.0, 0.5) { value ->
					distanceMax = value
					if (value != null) distanceMin = distanceMin?.coerceAtMost(value)
				}
				OptionalSlider("level", "min level", level, 100.0, 1.0) { level = it }
				if (base.takesLimit) OptionalSlider("limit", "limit", limit?.toDouble(), 10.0, 1.0) { limit = it?.toInt() }
				TextField("selector-builder-name", "name", name, "Steve or !Steve") { name = it }
				TextField("selector-builder-tag", "tag", tag, "boss or !boss") { tag = it }
				TextField("selector-builder-team", "team", team, "red or !red") { team = it }
			}
		}

		Div({ id(SELECTOR_TEXT_ID) }) { key(selector) { CodeBlock(selector, "mcfunction") } }
		Div({ id(SELECTOR_KOTLIN_ID) }) { key(kotlin) { CodeBlock(kotlin, "kotlin") } }
		highlightCodeIn(SELECTOR_TEXT_ID, selector)
		highlightCodeIn(SELECTOR_KOTLIN_ID, kotlin)
	}
}
