package io.github.ayfri.kore.website.components.doc

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import io.github.ayfri.kore.website.LandingVars
import io.github.ayfri.kore.website.components.common.CodeBlock
import io.github.ayfri.kore.website.components.common.McSprite
import io.github.ayfri.kore.website.components.common.Tab
import io.github.ayfri.kore.website.components.common.Tabs
import io.github.ayfri.kore.website.utils.alpha
import io.github.ayfri.kore.website.utils.highlightCodeIn
import io.github.ayfri.kore.website.utils.monoFont
import io.github.ayfri.kore.website.utils.smMax
import org.jetbrains.compose.web.attributes.step
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.dom.*
import kotlin.math.ceil
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round
import kotlin.math.sqrt
import kotlin.random.Random

private const val SAMPLE_COUNT = 10_000
private const val MAX_INT_BARS = 48
private const val FLOAT_BINS = 40
private const val FLOAT_STEP = 0.025
private const val MAX_SPRITES = 16
private const val ROLL_COUNT = 8
private const val KOTLIN_CODE_ID = "provider-explorer-kotlin"
private const val JSON_CODE_ID = "provider-explorer-json"

private typealias Values = Map<String, Double>

private fun Values.int(name: String) = getValue(name).toInt()

private fun Values.double(name: String) = getValue(name)

private fun Double.round3() = round(this * 1000) / 1000 + 0.0

/** Three decimals at most, no trailing zeros: `3.2`, `5`, `0.125`. */
private fun Double.display() = round3().toString()

/** Always carries a decimal, since the JS runtime would print `1.0` as `1`. */
private fun Double.asFloat() = round3().toString().let { if ('.' in it) it else "$it.0" }

/** Two decimals at most, for the plain-language reading. */
private fun Double.display2() = (round(this * 100) / 100 + 0.0).toString()

private fun Double.asPercent() = round(this * 1000).let { if (it < 1) "less than 0.1%" else "${it / 10}%" }

private fun jsonObject(type: String, vararg fields: Pair<String, String>) =
	(listOf("\"type\": \"minecraft:$type\"") + fields.map { (key, value) -> "\"$key\": ${value.replace("\n", "\n\t")}" })
		.joinToString(",\n\t", "{\n\t", "\n}")

private enum class ProviderKind(val label: String) {
	INT("Int"),
	FLOAT("Float"),
}

/** One slider of a provider. A [partner] makes both ends of a range keep `lower + gap <= upper`. */
private data class Param(
	val name: String,
	val min: Double,
	val max: Double,
	val step: Double,
	val default: Double,
	val gap: Double = 0.0,
	val isPlateau: Boolean = false,
	val label: String = name,
	val partner: String? = null,
	val upper: Boolean = false,
)

/** Ports the game's `Mth.randomBetweenInclusive`, `nextInt` and `nextGaussian` (Marsaglia polar), over a seeded [Random]. */
private class Sampler(private val random: Random) {
	private var spare: Double? = null

	fun between(min: Int, maxInclusive: Int) = nextInt(maxInclusive - min + 1) + min

	fun gaussian(): Double {
		spare?.let {
			spare = null
			return it
		}
		var x: Double
		var y: Double
		var radiusSquared: Double
		do {
			x = 2.0 * random.nextDouble() - 1.0
			y = 2.0 * random.nextDouble() - 1.0
			radiusSquared = x * x + y * y
		} while (radiusSquared >= 1.0 || radiusSquared == 0.0)
		val multiplier = sqrt(-2.0 * ln(radiusSquared) / radiusSquared)
		spare = y * multiplier
		return x * multiplier
	}

	fun nextInt(bound: Int) = random.nextInt(bound)

	fun unit() = random.nextDouble()
}

private class ProviderSpec(
	val kind: ProviderKind,
	val key: String,
	val function: String,
	val params: List<Param>,
	val sample: Sampler.(Values) -> Double,
	val kotlin: (Values) -> String,
	val json: (Values) -> String,
	val domain: (Values) -> Pair<Double, Double>,
) {
	val id = "${kind.name.lowercase()}-$key"

	/** The values shown by the controls: stored edits over defaults, the plateau capped at the `min`..`max` span. */
	fun resolve(edits: Map<String, Double>): Values {
		val values = params.associate { it.name to (edits["$id-${it.name}"] ?: it.default) }
		val plateau = params.firstOrNull { it.isPlateau } ?: return values
		return values + (plateau.name to values.getValue(plateau.name).coerceIn(0.0, span(values)))
	}

	fun limit(param: Param, values: Values) = if (param.isPlateau) span(values) else param.max

	private fun span(values: Values) = (values.double("max") - values.double("min")).round3()

	/** The new values of [param] and of its partner, keeping the pair ordered. */
	fun update(values: Values, param: Param, raw: Double): Map<String, Double> {
		val value = (round(raw / param.step) * param.step).round3().coerceIn(param.min, max(param.min, limit(param, values)))
		val partner = param.partner?.let { name -> params.first { it.name == name } } ?: return mapOf(param.name to value)
		val other = values.double(partner.name)
		return if (param.upper) {
			val lower = min(other, value - param.gap).round3().coerceAtLeast(partner.min)
			mapOf(partner.name to lower, param.name to max(value, lower + param.gap).round3())
		} else {
			val upper = max(other, value + param.gap).round3().coerceAtMost(partner.max)
			mapOf(partner.name to upper, param.name to min(value, upper - param.gap).round3())
		}
	}
}

private fun bounds(kind: ProviderKind, lower: String, upper: String, defaultLower: Double, defaultUpper: Double, gap: Double = 0.0): List<Param> {
	val limit = if (kind == ProviderKind.INT) 64.0 else 4.0
	val step = if (kind == ProviderKind.INT) 1.0 else FLOAT_STEP
	return listOf(
		Param(lower, -limit, limit, step, defaultLower, gap, partner = upper),
		Param(upper, -limit, limit, step, defaultUpper, gap, partner = lower, upper = true),
	)
}

private fun intBounds(lower: String, upper: String, defaultLower: Int, defaultUpper: Int) =
	bounds(ProviderKind.INT, lower, upper, defaultLower.toDouble(), defaultUpper.toDouble())

private fun intRange(key: String, function: String, defaultMin: Int, defaultMax: Int, draw: Sampler.(Int, Int) -> Int) = ProviderSpec(
	kind = ProviderKind.INT,
	key = key,
	function = function,
	params = intBounds("minInclusive", "maxInclusive", defaultMin, defaultMax),
	sample = { draw(it.int("minInclusive"), it.int("maxInclusive")).toDouble() },
	kotlin = { "$function(minInclusive = ${it.int("minInclusive")}, maxInclusive = ${it.int("maxInclusive")})" },
	json = { jsonObject(key, "min_inclusive" to it.int("minInclusive").toString(), "max_inclusive" to it.int("maxInclusive").toString()) },
	domain = { it.double("minInclusive") to it.double("maxInclusive") },
)

private val INT_SPECS = listOf(
	ProviderSpec(
		kind = ProviderKind.INT,
		key = "constant",
		function = "constant",
		params = listOf(Param("value", -64.0, 64.0, 1.0, 5.0)),
		sample = { it.double("value") },
		kotlin = { "constant(${it.int("value")})" },
		json = { it.int("value").toString() },
		domain = { it.double("value") to it.double("value") },
	),
	intRange("uniform", "uniform", 1, 5) { min, max -> between(min, max) },
	intRange("biased_to_bottom", "biasedToBottom", 0, 14) { min, max -> min + nextInt(nextInt(max - min + 1) + 1) },
	intRange("very_biased_to_bottom", "veryBiasedToBottom", 0, 14) { min, max ->
		min + nextInt(nextInt(nextInt(max - min + 1) + 1) + 1)
	},
	ProviderSpec(
		kind = ProviderKind.INT,
		key = "clamped",
		function = "clamped",
		params = intBounds("minInclusive", "maxInclusive", 4, 12) +
			intBounds("sourceMin", "sourceMax", 0, 20).map { it.copy(label = "source.${if (it.upper) "maxInclusive" else "minInclusive"}") },
		domain = {
			it.double("sourceMin").coerceIn(it.double("minInclusive"), it.double("maxInclusive")) to
				it.double("sourceMax").coerceIn(it.double("minInclusive"), it.double("maxInclusive"))
		},
		sample = {
			between(it.int("sourceMin"), it.int("sourceMax")).coerceIn(it.int("minInclusive"), it.int("maxInclusive")).toDouble()
		},
		kotlin = {
			"clamped(minInclusive = ${it.int("minInclusive")}, maxInclusive = ${it.int("maxInclusive")}, " +
				"source = uniform(minInclusive = ${it.int("sourceMin")}, maxInclusive = ${it.int("sourceMax")}))"
		},
		json = {
			jsonObject(
				"clamped",
				"min_inclusive" to it.int("minInclusive").toString(),
				"max_inclusive" to it.int("maxInclusive").toString(),
				"source" to jsonObject(
					"uniform",
					"min_inclusive" to it.int("sourceMin").toString(),
					"max_inclusive" to it.int("sourceMax").toString(),
				),
			)
		},
	),
	ProviderSpec(
		kind = ProviderKind.INT,
		key = "clamped_normal",
		function = "clampedNormal",
		params = intBounds("minInclusive", "maxInclusive", 1, 10) + listOf(
			Param("mean", -64.0, 64.0, 0.5, 5.0),
			Param("deviation", 0.1, 16.0, 0.1, 2.0),
		),
		domain = { it.double("minInclusive") to it.double("maxInclusive") },
		sample = {
			(it.double("mean") + gaussian() * it.double("deviation"))
				.coerceIn(it.double("minInclusive"), it.double("maxInclusive")).toInt().toDouble()
		},
		kotlin = {
			"clampedNormal(minInclusive = ${it.int("minInclusive")}, maxInclusive = ${it.int("maxInclusive")}, " +
				"mean = ${it.double("mean").asFloat()}f, deviation = ${it.double("deviation").asFloat()}f)"
		},
		json = {
			jsonObject(
				"clamped_normal",
				"min_inclusive" to it.int("minInclusive").toString(),
				"max_inclusive" to it.int("maxInclusive").toString(),
				"mean" to it.double("mean").asFloat(),
				"deviation" to it.double("deviation").asFloat(),
			)
		},
	),
	ProviderSpec(
		kind = ProviderKind.INT,
		key = "trapezoid",
		function = "trapezoid",
		params = intBounds("min", "max", 0, 10) + Param("plateau", 0.0, 64.0, 1.0, 4.0, isPlateau = true),
		domain = { it.double("min") to it.double("max") },
		sample = {
			val min = it.int("min")
			val max = it.int("max")
			val plateau = it.int("plateau")
			val range = max - min
			when {
				plateau == 0 && max == -min -> nextInt(max + 1) - nextInt(max + 1)
				plateau == range -> between(min, max)
				else -> {
					val plateauStart = (range - plateau) / 2
					min + between(0, range - plateauStart) + between(0, plateauStart)
				}
			}.toDouble()
		},
		kotlin = { "trapezoid(min = ${it.int("min")}, max = ${it.int("max")}, plateau = ${it.int("plateau")})" },
		json = { jsonObject("trapezoid", "min" to it.int("min").toString(), "max" to it.int("max").toString(), "plateau" to it.int("plateau").toString()) },
	),
)

private fun floatBounds(lower: String, upper: String, defaultLower: Double, defaultUpper: Double, gap: Double = 0.0) =
	bounds(ProviderKind.FLOAT, lower, upper, defaultLower, defaultUpper, gap)

private val FLOAT_SPECS = listOf(
	ProviderSpec(
		kind = ProviderKind.FLOAT,
		key = "constant",
		function = "constant",
		params = listOf(Param("value", -4.0, 4.0, FLOAT_STEP, 0.5)),
		sample = { it.double("value") },
		kotlin = { "constant(${it.double("value").asFloat()}f)" },
		json = { it.double("value").asFloat() },
		domain = { it.double("value") to it.double("value") },
	),
	ProviderSpec(
		kind = ProviderKind.FLOAT,
		key = "uniform",
		function = "uniform",
		params = floatBounds("minInclusive", "maxExclusive", 0.8, 1.2, gap = 0.05),
		domain = { it.double("minInclusive") to it.double("maxExclusive") },
		sample = { it.double("minInclusive") + unit() * (it.double("maxExclusive") - it.double("minInclusive")) },
		kotlin = { "uniform(minInclusive = ${it.double("minInclusive").asFloat()}f, maxExclusive = ${it.double("maxExclusive").asFloat()}f)" },
		json = {
			jsonObject(
				"uniform",
				"min_inclusive" to it.double("minInclusive").asFloat(),
				"max_exclusive" to it.double("maxExclusive").asFloat(),
			)
		},
	),
	ProviderSpec(
		kind = ProviderKind.FLOAT,
		key = "clamped_normal",
		function = "clampedNormal",
		params = listOf(
			Param("mean", -4.0, 4.0, FLOAT_STEP, 0.5),
			Param("deviation", 0.1, 16.0, FLOAT_STEP, 0.2),
		) + floatBounds("min", "max", 0.0, 1.0),
		domain = { it.double("min") to it.double("max") },
		sample = { (it.double("mean") + gaussian() * it.double("deviation")).coerceIn(it.double("min"), it.double("max")) },
		kotlin = {
			"clampedNormal(mean = ${it.double("mean").asFloat()}f, deviation = ${it.double("deviation").asFloat()}f, " +
				"min = ${it.double("min").asFloat()}f, max = ${it.double("max").asFloat()}f)"
		},
		json = {
			jsonObject(
				"clamped_normal",
				"mean" to it.double("mean").asFloat(),
				"deviation" to it.double("deviation").asFloat(),
				"min" to it.double("min").asFloat(),
				"max" to it.double("max").asFloat(),
			)
		},
	),
	ProviderSpec(
		kind = ProviderKind.FLOAT,
		key = "trapezoid",
		function = "trapezoid",
		params = floatBounds("min", "max", 0.0, 2.0) + Param("plateau", 0.0, 8.0, FLOAT_STEP, 0.5, isPlateau = true),
		domain = { it.double("min") to it.double("max") },
		sample = {
			val range = it.double("max") - it.double("min")
			val plateauStart = (range - it.double("plateau")) / 2
			it.double("min") + unit() * (range - plateauStart) + unit() * plateauStart
		},
		kotlin = {
			"trapezoid(min = ${it.double("min").asFloat()}f, max = ${it.double("max").asFloat()}f, plateau = ${it.double("plateau").asFloat()}f)"
		},
		json = {
			jsonObject(
				"trapezoid",
				"min" to it.double("min").asFloat(),
				"max" to it.double("max").asFloat(),
				"plateau" to it.double("plateau").asFloat(),
			)
		},
	),
)

private fun ProviderKind.specs() = if (this == ProviderKind.INT) INT_SPECS else FLOAT_SPECS

private class Sprite(val path: String, val label: String, val stacked: Boolean = false, val tint: CSSColorValue? = null)

/** A real vanilla provider: where it is [file] and [field], what it rolls ([unit], [note]) and how a roll is drawn ([sprite]). */
private class Preset(
	val label: String,
	val file: String,
	val field: String,
	val note: String,
	val kind: ProviderKind,
	val key: String,
	val values: Values,
	val unit: Pair<String, String>? = null,
	val sprite: Sprite? = null,
)

private val PRESETS = listOf(
	Preset(
		label = "Cave tunnels",
		file = "worldgen/carver/cave.json",
		field = "count",
		note = "tunnels per carve attempt.",
		kind = ProviderKind.INT,
		key = "very_biased_to_bottom",
		values = mapOf("minInclusive" to 0.0, "maxInclusive" to 14.0),
		unit = "tunnel" to "tunnels",
		sprite = Sprite("block/stone", "tunnel"),
	),
	Preset(
		label = "Cave thickness",
		file = "worldgen/carver/cave.json",
		field = "thickness",
		note = "breadth and height of tunnels.",
		kind = ProviderKind.FLOAT,
		key = "trapezoid",
		values = mapOf("min" to 0.0, "max" to 3.0, "plateau" to 1.0),
	),
	Preset(
		label = "Canyon thickness",
		file = "worldgen/carver/canyon.json",
		field = "shape.thickness",
		note = "breadth and height of the canyon.",
		kind = ProviderKind.FLOAT,
		key = "trapezoid",
		values = mapOf("min" to 0.0, "max" to 6.0, "plateau" to 2.0),
	),
	Preset(
		label = "Canyon rotation",
		file = "worldgen/carver/canyon.json",
		field = "vertical_rotation",
		note = "vertical rotation applied as the canyon extends.",
		kind = ProviderKind.FLOAT,
		key = "uniform",
		values = mapOf("minInclusive" to -0.125, "maxExclusive" to 0.125),
	),
	Preset(
		label = "Dripstone tries",
		file = "worldgen/placed_feature/large_dripstone.json",
		field = "count",
		note = "placement repeats per chunk.",
		kind = ProviderKind.INT,
		key = "uniform",
		values = mapOf("minInclusive" to 10.0, "maxInclusive" to 48.0),
		unit = "try" to "tries",
		sprite = Sprite("block/dripstone_block", "dripstone try"),
	),
	Preset(
		label = "Forest flowers",
		file = "worldgen/placed_feature/forest_flowers.json",
		field = "count",
		note = "placement repeats per chunk, clamped to 0 or 1.",
		kind = ProviderKind.INT,
		key = "clamped",
		values = mapOf("minInclusive" to 0.0, "maxInclusive" to 1.0, "sourceMin" to -3.0, "sourceMax" to 1.0),
		unit = "try" to "tries",
		sprite = Sprite("block/peony_top", "flower try"),
	),
	Preset(
		label = "Diamond veins",
		file = "worldgen/placed_feature/ore_diamond.json",
		field = "count",
		note = "small diamond veins attempted per chunk.",
		kind = ProviderKind.INT,
		key = "constant",
		values = mapOf("value" to 7.0),
		unit = "try" to "tries",
		sprite = Sprite("block/diamond_ore", "vein try"),
	),
	Preset(
		label = "Mega pine crown",
		file = "worldgen/feature/mega_pine.json",
		field = "foliage_placer.crown_height",
		note = "height of the crown, in blocks.",
		kind = ProviderKind.INT,
		key = "uniform",
		values = mapOf("minInclusive" to 3.0, "maxInclusive" to 7.0),
		unit = "block" to "blocks",
		/** `#619961` is the fixed evergreen foliage color vanilla gives spruce leaves. */
		sprite = Sprite("block/spruce_leaves", "block of crown height", stacked = true, tint = Color("#619961")),
	),
)

private class Bar(val fraction: Double, val title: String)

/** Binned samples over the provider's possible values `from`..`to`, and the figures the plain-language reading quotes. */
private class Histogram(
	val bars: List<Bar>,
	val from: Double,
	val to: Double,
	val lowest: Double,
	val median: Double,
	val ninetieth: Double,
	val highest: Double,
	val shareAtHighest: Double,
) {
	/** What the samples say, in words: median, 90th percentile, and the highest value with its share. */
	fun reading(kind: ProviderKind, unit: Pair<String, String>?): String {
		fun Double.withUnit() = display2() + unit?.let { (singular, plural) -> " ${if (this == 1.0) singular else plural}" }.orEmpty()

		if (lowest == highest) return "Every roll gives ${highest.withUnit()}."
		if (kind == ProviderKind.INT) {
			val half = if (median == lowest) "At least half the rolls give the minimum, ${median.withUnit()}" else "Half the rolls give ${median.withUnit()} or less"
			return "$half, 9 in 10 give ${ninetieth.display2()} or less. " +
				"The maximum, ${highest.display2()}, comes up ${shareAtHighest.asPercent()} of the time."
		}
		val clamped = if (shareAtHighest >= 0.01) " It comes up exactly ${shareAtHighest.asPercent()} of the time, where the value is clamped." else ""
		return "Half the rolls are ${median.display2()} or less, 9 in 10 are ${ninetieth.display2()} or less, and the highest seen is ${highest.display2()}.$clamped"
	}

	companion object {
		/** Bins [samples]: one bar per integer (bucketed past [MAX_INT_BARS]), [FLOAT_BINS] bins for floats, one bar if `from == to`. */
		fun of(kind: ProviderKind, samples: DoubleArray, from: Double, to: Double): Histogram {
			val sorted = samples.sortedArray()
			val highest = sorted.last()
			val counts: List<Int>
			val label: (Int) -> String

			if (kind == ProviderKind.INT) {
				val exact = IntArray((to - from).toInt() + 1)
				samples.forEach { exact[(it - from).toInt().coerceIn(exact.indices)]++ }
				val bucket = ceil(exact.size / MAX_INT_BARS.toDouble()).toInt()
				counts = exact.asList().chunked(bucket) { it.sum() }
				label = { index ->
					val first = from + index * bucket
					if (bucket == 1) first.display() else "${first.display()} to ${min(first + bucket - 1, to).display()}"
				}
			} else if (from == to) {
				counts = listOf(samples.size)
				label = { from.display() }
			} else {
				val bins = IntArray(FLOAT_BINS)
				samples.forEach { bins[((it - from) / (to - from) * FLOAT_BINS).toInt().coerceIn(bins.indices)]++ }
				counts = bins.asList()
				label = { index ->
					val width = (to - from) / FLOAT_BINS
					"${(from + index * width).display()} to ${(from + (index + 1) * width).display()}"
				}
			}

			val peak = counts.max().toDouble()
			return Histogram(
				bars = counts.mapIndexed { index, count -> Bar(count / peak, "${label(index)}: ${(count.toDouble() / samples.size).asPercent()}") },
				from = from,
				to = to,
				lowest = sorted.first(),
				median = sorted[sorted.size / 2],
				ninetieth = sorted[(sorted.size * 0.9).toInt()],
				highest = highest,
				shareAtHighest = samples.count { it == highest }.toDouble() / samples.size,
			)
		}
	}
}

/** Samples int and float providers like the game does, and draws their distribution with the matching Kotlin call and JSON. */
@Composable
fun ProviderExplorer() {
	Style(ProviderExplorerStyle)

	var spec by remember { mutableStateOf(INT_SPECS[1]) }
	var edits by remember { mutableStateOf<Map<String, Double>>(emptyMap()) }
	var preset by remember { mutableStateOf<Preset?>(null) }
	var rerolls by remember { mutableStateOf(0) }
	val values = spec.resolve(edits)
	val histogram = remember(spec, values) {
		val sampler = Sampler(Random(0))
		val (from, to) = spec.domain(values)
		Histogram.of(spec.kind, DoubleArray(SAMPLE_COUNT) { spec.sample(sampler, values) }, from, to)
	}
	val rolls = remember(spec, values, rerolls) {
		val sampler = Sampler(Random(rerolls + 1))
		List(ROLL_COUNT) { spec.sample(sampler, values) }
	}
	val unit = preset?.unit
	val reading = histogram.reading(spec.kind, unit)
	val kotlin = spec.kotlin(values)
	val json = spec.json(values)

	Div({ classes(ProviderExplorerStyle.explorer) }) {
		PresetRow(preset, preset?.values?.any { (name, value) -> values[name] != value } == true) { chosen ->
			if (chosen == preset) {
				preset = null
				return@PresetRow
			}
			val target = chosen.kind.specs().first { it.key == chosen.key }
			spec = target
			edits = edits.filterKeys { !it.startsWith("${target.id}-") } + chosen.values.mapKeys { (name, _) -> "${target.id}-$name" }
			preset = chosen
		}

		Div({ classes(ProviderExplorerStyle.toolbar) }) {
			PillGroup("Provider kind", ProviderKind.entries, spec.kind, ProviderKind::label) { kind ->
				val specs = kind.specs()
				spec = specs.firstOrNull { it.key == spec.key } ?: specs.first()
				preset = null
			}
			PillGroup("Provider type", spec.kind.specs(), spec, ProviderSpec::function) {
				spec = it
				preset = null
			}
		}

		Div({ classes(ProviderExplorerStyle.controls) }) {
			spec.params.forEach { param ->
				ParamControl(spec, param, values) { value ->
					edits = edits + spec.update(values, param, value).mapKeys { (name, _) -> "${spec.id}-$name" }
				}
			}
		}

		Div({ classes(ProviderExplorerStyle.chart) }) {
			HistogramBars(histogram, "$SAMPLE_COUNT samples. $reading")
			Div({ classes(ProviderExplorerStyle.reading) }) { Text(reading) }
		}

		RollStrip(rolls, spec.kind, preset?.sprite, histogram.from, histogram.to) { rerolls++ }

		Tabs(
			listOf(
				Tab("Kotlin") { Div({ id(KOTLIN_CODE_ID) }) { key(kotlin) { CodeBlock(kotlin, "kotlin") } } },
				Tab("JSON") { Div({ id(JSON_CODE_ID) }) { key(json) { CodeBlock(json, "json") } } },
			),
		)
		highlightCodeIn(KOTLIN_CODE_ID, kotlin)
		highlightCodeIn(JSON_CODE_ID, json)
	}
}

@Composable
private fun <T : Any> PillGroup(label: String, options: List<T>, selected: T?, text: (T) -> String, onSelect: (T) -> Unit) {
	Div({
		classes(ProviderExplorerStyle.group)
		attr("aria-label", label)
		attr("role", "group")
	}) {
		options.forEach { option ->
			Button({
				classes(ProviderExplorerStyle.pill)
				if (option == selected) classes(ProviderExplorerStyle.pillActive)
				attr("aria-pressed", (option == selected).toString())
				attr("type", "button")
				onClick { onSelect(option) }
			}) { Text(text(option)) }
		}
	}
}

@Composable
private fun ParamControl(spec: ProviderSpec, param: Param, values: Values, onChange: (Double) -> Unit) {
	val value = values.double(param.name)
	val limit = spec.limit(param, values)
	val inputId = "provider-explorer-${spec.id}-${param.name}"

	Div({ classes(ProviderExplorerStyle.control) }) {
		Label(inputId, { classes(ProviderExplorerStyle.controlLabel) }) { Text(param.label) }
		Div({ classes(ProviderExplorerStyle.controlRow) }) {
			RangeInput(value, param.min, limit, param.step) {
				classes(ProviderExplorerStyle.slider)
				attr("aria-label", "${param.label} slider")
				onInput { event -> event.value?.toDouble()?.takeUnless { it.isNaN() }?.let(onChange) }
			}
			NumberInput(value, param.min, limit) {
				id(inputId)
				classes(ProviderExplorerStyle.number)
				step(param.step)
				onInput { event -> event.value?.toDouble()?.takeUnless { it.isNaN() }?.let(onChange) }
			}
		}
	}
}

@Composable
private fun PresetRow(selected: Preset?, edited: Boolean, onSelect: (Preset) -> Unit) {
	Div({ classes(ProviderExplorerStyle.presets) }) {
		Span({ classes(ProviderExplorerStyle.presetsLabel) }) { Text("Try a vanilla value") }
		PillGroup("Vanilla presets", PRESETS, selected, Preset::label, onSelect)
		selected?.let {
			Div({ classes(ProviderExplorerStyle.caption) }) {
				Text("from ")
				Span({ classes(ProviderExplorerStyle.captionFile) }) { Text(it.file) }
				Text(", ")
				Span({ classes(ProviderExplorerStyle.captionFile) }) { Text(it.field) }
				Text(": ${it.note}${if (edited) " (edited)" else ""}")
			}
		}
	}
}

@Composable
private fun HistogramBars(histogram: Histogram, label: String) {
	val singleBar = histogram.bars.size == 1

	Div({
		classes(ProviderExplorerStyle.bars)
		if (singleBar) classes(ProviderExplorerStyle.barsSingle)
		attr("aria-label", label)
		attr("role", "img")
	}) {
		histogram.bars.forEach { bar ->
			Div({
				classes(ProviderExplorerStyle.barSlot)
				if (singleBar) classes(ProviderExplorerStyle.barSlotSingle)
				attr("title", bar.title)
			}) {
				Div({
					classes(ProviderExplorerStyle.bar)
					style { height((if (bar.fraction > 0) max(bar.fraction * 100, 1.0) else 0.0).percent) }
				})
			}
		}
	}
	Div({
		classes(ProviderExplorerStyle.axis)
		if (singleBar) classes(ProviderExplorerStyle.axisSingle)
	}) {
		Span { Text(histogram.from.display()) }
		if (!singleBar) Span { Text(histogram.to.display()) }
	}
}

/** [ROLL_COUNT] samples drawn like in game: [sprite]s for an int with a vanilla preset, a bar over `from`..`to` otherwise. */
@Composable
private fun RollStrip(rolls: List<Double>, kind: ProviderKind, sprite: Sprite?, from: Double, to: Double, onReroll: () -> Unit) {
	val stacked = kind == ProviderKind.INT && sprite?.stacked == true

	Div({ classes(ProviderExplorerStyle.rolls) }) {
		Div({ classes(ProviderExplorerStyle.rollsHeader) }) {
			Span({ classes(ProviderExplorerStyle.rollsTitle) }) { Text("$ROLL_COUNT rolls") }
			Button({
				classes(ProviderExplorerStyle.pill)
				attr("type", "button")
				onClick { onReroll() }
			}) { Text("Reroll") }
		}
		Div({ classes(if (stacked) ProviderExplorerStyle.rollStacks else ProviderExplorerStyle.rollGrid) }) {
			rolls.forEach { roll ->
				when {
					kind == ProviderKind.INT && sprite != null && stacked -> StackRoll(roll.toInt(), sprite)
					kind == ProviderKind.INT && sprite != null -> SpriteRoll(roll.toInt(), sprite)
					else -> BarRoll(roll, from, to)
				}
			}
		}
	}
}

@Composable
private fun SpriteRoll(count: Int, sprite: Sprite) {
	Div({ classes(ProviderExplorerStyle.roll) }) {
		Span({ classes(ProviderExplorerStyle.rollValue) }) { Text(count.toString()) }
		Div({ classes(ProviderExplorerStyle.sprites) }) {
			repeat(count.coerceIn(0, MAX_SPRITES)) { McSprite(sprite.path, sprite.label, sprite.tint) }
			if (count > MAX_SPRITES) Span({ classes(ProviderExplorerStyle.rollMore) }) { Text("+${count - MAX_SPRITES}") }
		}
	}
}

@Composable
private fun StackRoll(count: Int, sprite: Sprite) {
	Div({ classes(ProviderExplorerStyle.rollStack) }) {
		Div({ classes(ProviderExplorerStyle.stack) }) {
			repeat(count.coerceIn(0, MAX_SPRITES)) { McSprite(sprite.path, sprite.label, sprite.tint) }
			if (count > MAX_SPRITES) Span({ classes(ProviderExplorerStyle.rollMore) }) { Text("+${count - MAX_SPRITES}") }
		}
		Span({ classes(ProviderExplorerStyle.rollValue) }) { Text(count.toString()) }
	}
}

@Composable
private fun BarRoll(value: Double, from: Double, to: Double) {
	val fraction = if (to == from) 1.0 else ((value - from) / (to - from)).coerceIn(0.0, 1.0)

	Div({ classes(ProviderExplorerStyle.roll) }) {
		Span({ classes(ProviderExplorerStyle.rollValue) }) { Text(value.display2()) }
		Div({ classes(ProviderExplorerStyle.track) }) {
			Div({
				classes(ProviderExplorerStyle.fill)
				style { width(max(fraction * 100, 1.0).percent) }
			})
		}
	}
}

private object ProviderExplorerStyle : StyleSheet() {
	val axis by style {
		color(LandingVars.Muted.value())
		display(DisplayStyle.Flex)
		fontSize(0.75.cssRem)
		justifyContent(JustifyContent.SpaceBetween)
		marginTop(0.35.cssRem)
		monoFont()
	}

	val axisSingle by style {
		justifyContent(JustifyContent.Center)
	}

	val bar by style {
		backgroundColor(LandingVars.Accent.value())
		width(100.percent)
	}

	val barSlot by style {
		alignItems(AlignItems.FlexEnd)
		display(DisplayStyle.Flex)
		flexGrow(1)
		flexShrink(1)
		height(100.percent)
		minWidth(0.px)
		width(0.px)

		hover(self) style {
			backgroundColor(LandingVars.Accent.value().alpha(0.12))
		}
	}

	val barSlotSingle by style {
		flexGrow(0)
		width(4.cssRem)
	}

	val bars by style {
		borderBottom(1.px, LineStyle.Solid, LandingVars.Border.value())
		display(DisplayStyle.Flex)
		gap(2.px)
		height(9.cssRem)

		smMax(self) {
			height(7.cssRem)
		}
	}

	val barsSingle by style {
		justifyContent(JustifyContent.Center)
	}

	val caption by style {
		color(LandingVars.Muted.value())
		fontSize(0.8.cssRem)
	}

	val captionFile by style {
		color(LandingVars.Text.value())
		monoFont()
	}

	val chart by style {
		backgroundColor(LandingVars.Pane.value())
		border(1.px, LineStyle.Solid, LandingVars.Border.value())
		borderRadius(0.8.cssRem)
		minWidth(0.px)
		padding(1.cssRem)
	}

	val control by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.3.cssRem)
		minWidth(0.px)
	}

	val controlLabel by style {
		color(LandingVars.Muted.value())
		fontSize(0.75.cssRem)
		monoFont()
	}

	val controlRow by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		gap(0.7.cssRem)
		minWidth(0.px)
	}

	val controls by style {
		display(DisplayStyle.Grid)
		gap(0.9.cssRem, 1.5.cssRem)
		gridTemplateColumns("repeat(2, minmax(0, 1fr))")

		smMax(self) {
			gridTemplateColumns("minmax(0, 1fr)")
		}
	}

	val explorer by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(1.cssRem)
		margin(1.5.cssRem, 0.px)
		minWidth(0.px)
		width(100.percent)
	}

	val fill by style {
		backgroundColor(LandingVars.Accent.value())
		height(100.percent)
	}

	val group by style {
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(0.35.cssRem)
	}

	val number by style {
		backgroundColor(LandingVars.Surface.value())
		border(1.px, LineStyle.Solid, LandingVars.Border.value())
		borderRadius(0.4.cssRem)
		boxSizing(BoxSizing.BorderBox)
		color(LandingVars.Text.value())
		fontSize(0.85.cssRem)
		monoFont()
		padding(0.3.cssRem, 0.5.cssRem)
		width(5.5.cssRem)
	}

	val pill by style {
		backgroundColor(LandingVars.Card.value())
		border(1.px, LineStyle.Solid, LandingVars.Border.value())
		borderRadius(999.px)
		color(LandingVars.Muted.value())
		cursor(Cursor.Pointer)
		fontSize(0.8.cssRem)
		monoFont()
		padding(0.3.cssRem, 0.8.cssRem)
	}

	val pillActive by style {
		backgroundColor(LandingVars.Accent.value().alpha(0.18))
		border(1.px, LineStyle.Solid, LandingVars.Accent.value())
		color(LandingVars.Text.value())
	}

	val presets by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.5.cssRem)
	}

	val presetsLabel by style {
		color(LandingVars.Muted.value())
		fontSize(0.75.cssRem)
		monoFont()
	}

	val reading by style {
		color(LandingVars.Text.value())
		fontSize(0.95.cssRem)
		marginTop(0.9.cssRem)
	}

	val roll by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		gap(0.6.cssRem)
		minWidth(0.px)
	}

	val rollGrid by style {
		display(DisplayStyle.Grid)
		gap(0.5.cssRem, 1.5.cssRem)
		gridTemplateColumns("repeat(2, minmax(0, 1fr))")

		smMax(self) {
			gridTemplateColumns("minmax(0, 1fr)")
		}
	}

	val rollMore by style {
		color(LandingVars.Muted.value())
		fontSize(0.75.cssRem)
		monoFont()
	}

	val rollStack by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.3.cssRem)
		justifyContent(JustifyContent.FlexEnd)
	}

	val rollStacks by style {
		alignItems(AlignItems.FlexEnd)
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(0.8.cssRem, 1.2.cssRem)
	}

	val rollValue by style {
		color(LandingVars.Text.value())
		flexShrink(0)
		fontSize(0.85.cssRem)
		minWidth(3.cssRem)
		monoFont()
	}

	val rolls by style {
		backgroundColor(LandingVars.Pane.value())
		border(1.px, LineStyle.Solid, LandingVars.Border.value())
		borderRadius(0.8.cssRem)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.8.cssRem)
		minWidth(0.px)
		padding(1.cssRem)
	}

	val rollsHeader by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		justifyContent(JustifyContent.SpaceBetween)
	}

	val rollsTitle by style {
		color(LandingVars.Muted.value())
		fontSize(0.75.cssRem)
		monoFont()
	}

	val slider by style {
		accentColor(AccentColor.of(LandingVars.Accent.value()))
		cursor(Cursor.Pointer)
		flexGrow(1)
		margin(0.px)
		minWidth(0.px)
	}

	val sprites by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		fontSize(0.9.cssRem)
		minWidth(0.px)
	}

	val stack by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.ColumnReverse)
		fontSize(0.9.cssRem)
	}

	val toolbar by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.6.cssRem)
	}

	val track by style {
		backgroundColor(LandingVars.Surface.value())
		flexGrow(1)
		height(0.6.cssRem)
		minWidth(0.px)
	}
}
