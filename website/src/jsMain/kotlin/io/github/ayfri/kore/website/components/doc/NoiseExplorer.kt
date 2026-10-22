package io.github.ayfri.kore.website.components.doc

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import io.github.ayfri.kore.website.LandingVars
import io.github.ayfri.kore.website.components.common.CodeBlock
import io.github.ayfri.kore.website.components.common.McSprite
import io.github.ayfri.kore.website.components.common.Tab
import io.github.ayfri.kore.website.components.common.Tabs
import io.github.ayfri.kore.website.utils.highlightCodeIn
import io.github.ayfri.kore.website.utils.monoFont
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.dom.*
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.set
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLElement
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.round
import kotlin.random.Random

/** Declared before [CACHE_DOUBLES], which reads it: a top-level constant can't reference one declared later in the file. */
private const val MAP_PX = 128
private const val CACHE_DOUBLES = 80 * 2 * MAP_PX * MAP_PX
private const val INPUT_FACTOR = 1.0181268882175227
private const val MAX_OCTAVES = 16
private const val OCTAVE_SAMPLES = 256
private const val SEA_LEVEL = 63
private const val SURFACE_Y = 72
private const val TERRAIN_COLUMNS = 256
private const val TERRAIN_ROWS = 64
private const val NOISE_JSON_ID = "noise-explorer-json"
private const val NOISE_KOTLIN_ID = "noise-explorer-kotlin"
private const val DISCLAIMER = "Same algorithm and octave scales as the game, with another seed, so features match in size but not in place. " +
	"Noise coordinates are block coordinates here (xz_scale 1). Values are clamped to -1..1 for the colors."

private val AREAS = listOf(512, 2048, 8192)

private val GRADIENTS = intArrayOf(
	1, 1, 0, -1, 1, 0, 1, -1, 0, -1, -1, 0,
	1, 0, 1, -1, 0, 1, 1, 0, -1, -1, 0, -1,
	0, 1, 1, 0, -1, 1, 0, 1, -1, 0, -1, -1,
	1, 1, 0, 0, -1, 1, -1, 1, 0, 0, -1, -1,
)

/** Always carries a decimal, since the JS runtime prints `1.0` as `1`. */
private fun Double.asDouble() = toString().let { if ('.' in it || 'e' in it) it else "$it.0" }

/** Two decimals, with `-0.0` turned into `0.0`. */
private fun Double.readout() = (round(this * 100) / 100 + 0.0).toString()

/** Rounds to the nearest `1 / perUnit`, so the result prints without floating point noise. */
private fun Double.snapTo(perUnit: Int) = round(this * perUnit) / perUnit + 0.0

private fun Double.asBlocks() = (if (this >= 1) toInt().toString() else "1/${(1 / this).toInt()}") + if (this > 1) " blocks" else " block"

private fun String.camelCase() = replace(Regex("_(.)")) { it.groupValues[1].uppercase() }

private fun argb(alpha: Int, red: Int, green: Int, blue: Int) = (alpha shl 24) or (red shl 16) or (green shl 8) or blue

/** A key that stays the same for one absolute [octave], so zeroing another octave leaves it untouched. */
private fun octaveKey(seed: Int, salt: Int, octave: Int): Int {
	var hash = seed * 1_000_003 + salt * 1_009 + octave
	hash = (hash xor (hash ushr 16)) * -2048144789
	hash = (hash xor (hash ushr 13)) * -1028477387
	return hash xor (hash ushr 16)
}

private class ImprovedNoise(random: Random) {
	private val xo = random.nextDouble() * 256
	private val yo = random.nextDouble() * 256
	private val zo = random.nextDouble() * 256
	private val p = IntArray(256) { it }.also { table ->
		for (k in 0 until 256) {
			val j = random.nextInt(256 - k)
			val swapped = table[k]
			table[k] = table[k + j]
			table[k + j] = swapped
		}
	}

	private fun fade(t: Double) = t * t * t * (t * (t * 6 - 15) + 10)

	private fun grad(hash: Int, x: Double, y: Double, z: Double): Double {
		val index = (hash and 15) * 3
		return GRADIENTS[index] * x + GRADIENTS[index + 1] * y + GRADIENTS[index + 2] * z
	}

	private fun lerp(t: Double, a: Double, b: Double) = a + t * (b - a)

	private fun perm(index: Int) = p[index and 255]

	fun noise(x: Double, y: Double, z: Double): Double {
		val dx = x + xo
		val dy = y + yo
		val dz = z + zo
		val ix = floor(dx).toInt()
		val iy = floor(dy).toInt()
		val iz = floor(dz).toInt()
		val fx = dx - ix
		val fy = dy - iy
		val fz = dz - iz
		val a = perm(ix)
		val b = perm(ix + 1)
		val aa = perm(a + iy)
		val ab = perm(a + iy + 1)
		val ba = perm(b + iy)
		val bb = perm(b + iy + 1)
		val d000 = grad(perm(aa + iz), fx, fy, fz)
		val d100 = grad(perm(ba + iz), fx - 1, fy, fz)
		val d010 = grad(perm(ab + iz), fx, fy - 1, fz)
		val d110 = grad(perm(bb + iz), fx - 1, fy - 1, fz)
		val d001 = grad(perm(aa + iz + 1), fx, fy, fz - 1)
		val d101 = grad(perm(ba + iz + 1), fx - 1, fy, fz - 1)
		val d011 = grad(perm(ab + iz + 1), fx, fy - 1, fz - 1)
		val d111 = grad(perm(bb + iz + 1), fx - 1, fy - 1, fz - 1)
		val u = fade(fx)
		val v = fade(fy)
		val w = fade(fz)
		return lerp(
			w,
			lerp(v, lerp(u, d000, d100), lerp(u, d010, d110)),
			lerp(v, lerp(u, d001, d101), lerp(u, d011, d111)),
		)
	}
}

private enum class NoiseView(val label: String) {
	MAP("Map"),
	TERRAIN("Terrain"),
	OCTAVES("Octaves"),
}

/** The points a [view] samples, [area] being 0 for the terrain slice. */
private data class NoiseGrid(val view: NoiseView, val area: Int) {
	val size
		get() = when (view) {
			NoiseView.MAP -> MAP_PX * MAP_PX
			NoiseView.TERRAIN -> TERRAIN_COLUMNS * TERRAIN_ROWS
			NoiseView.OCTAVES -> OCTAVE_SAMPLES
		}
}

/** The coordinates of every point of [grid]: map cell centers at `y = 0`, terrain cells at `z = 0`, curve samples along `x`. */
private class NoisePoints(grid: NoiseGrid) {
	val xs = DoubleArray(grid.size) {
		when (grid.view) {
			NoiseView.MAP -> (it % MAP_PX + 0.5) * grid.area / MAP_PX
			NoiseView.TERRAIN -> it % TERRAIN_COLUMNS * 2.0
			NoiseView.OCTAVES -> (it + 0.5) * grid.area / OCTAVE_SAMPLES
		}
	}

	val ys = DoubleArray(grid.size) {
		if (grid.view == NoiseView.TERRAIN) ((TERRAIN_ROWS - 1 - it / TERRAIN_COLUMNS) * 2).toDouble() else 0.0
	}

	val zs = DoubleArray(grid.size) {
		when (grid.view) {
			NoiseView.MAP -> (it / MAP_PX + 0.5) * grid.area / MAP_PX
			NoiseView.TERRAIN -> 0.0
			NoiseView.OCTAVES -> octavesZ(grid.area)
		}
	}
}

/** The row of the map the octave curves follow. */
private fun octavesZ(area: Int) = (MAP_PX / 2 + 0.5) * area / MAP_PX

/**
 * Samples the game's `NormalNoise` on a [NoiseGrid], one pair of Perlin grids per absolute octave and seed.
 * The value is linear in the octave modifiers, so those grids are cached (least recently used first out)
 * and a modifier or `baseAmplitude` change only recombines them.
 */
private class NoiseSampler {
	private class OctaveSamples(val first: DoubleArray, val second: DoubleArray)

	private data class SamplesKey(val grid: NoiseGrid, val seed: Int, val octave: Int)

	private val cache = LinkedHashMap<SamplesKey, OctaveSamples>()
	private var cachedDoubles = 0
	private val points = HashMap<NoiseGrid, NoisePoints>()

	private fun samples(grid: NoiseGrid, seed: Int, octave: Int): OctaveSamples {
		val key = SamplesKey(grid, seed, octave)
		cache.remove(key)?.let {
			cache[key] = it
			return it
		}
		val frequency = 2.0.pow(octave)
		val first = ImprovedNoise(Random(octaveKey(seed, 0, octave)))
		val second = ImprovedNoise(Random(octaveKey(seed, 1, octave)))
		val at = points.getOrPut(grid) { NoisePoints(grid) }
		val sampled = OctaveSamples(DoubleArray(grid.size), DoubleArray(grid.size))
		for (point in 0 until grid.size) {
			val x = at.xs[point]
			val y = at.ys[point]
			val z = at.zs[point]
			sampled.first[point] = first.noise(x * frequency, y * frequency, z * frequency)
			sampled.second[point] = second.noise(x * INPUT_FACTOR * frequency, y * INPUT_FACTOR * frequency, z * INPUT_FACTOR * frequency)
		}
		cache[key] = sampled
		cachedDoubles += 2 * grid.size
		while (cachedDoubles > CACHE_DOUBLES) cachedDoubles -= 2 * cache.remove(cache.keys.first())!!.first.size
		return sampled
	}

	/** The noise of octave [index] alone, as the Octaves view draws it. */
	fun octave(grid: NoiseGrid, seed: Int, config: NoiseConfig, index: Int): DoubleArray {
		val values = DoubleArray(grid.size)
		val amplitude = config.modifiers[index]
		if (amplitude == 0.0) return values
		val sampled = samples(grid, seed, config.baseOctave + index)
		val lowestValue = config.lowestValue
		val divisor = 2.0.pow(index)
		val normalization = config.normalization
		for (point in values.indices) {
			values[point] =
				(amplitude * sampled.first[point] * lowestValue / divisor + amplitude * sampled.second[point] * lowestValue / divisor) * normalization
		}
		return values
	}

	fun value(grid: NoiseGrid, seed: Int, config: NoiseConfig): DoubleArray {
		val first = DoubleArray(grid.size)
		val second = DoubleArray(grid.size)
		var weight = config.lowestValue
		config.modifiers.forEachIndexed { index, amplitude ->
			if (amplitude != 0.0) {
				val sampled = samples(grid, seed, config.baseOctave + index)
				for (point in first.indices) {
					first[point] += amplitude * sampled.first[point] * weight
					second[point] += amplitude * sampled.second[point] * weight
				}
			}
			weight /= 2
		}
		val normalization = config.normalization
		for (point in first.indices) first[point] = (first[point] + second[point]) * normalization
		return first
	}
}

/** One `worldgen/noise` file: octave `index` of [modifiers] sits at `baseOctave + index`. */
private data class NoiseConfig(val baseOctave: Int, val modifiers: List<Double>, val baseAmplitude: Double) {
	val allOnes get() = modifiers.all { it == 1.0 }

	/** The weight of the first octave, each next one halves it. */
	val lowestValue get() = 2.0.pow(modifiers.size - 1) / (2.0.pow(modifiers.size) - 1)

	/** The pre-26.x `NormalNoise` scaling times `baseAmplitude`, the 26.x `normalize` formula isn't published. */
	val normalization: Double
		get() {
			val lowest = modifiers.indexOfFirst { it != 0.0 }
			val span = modifiers.indexOfLast { it != 0.0 } - lowest
			return if (lowest < 0) 0.0 else (1.0 / 6.0) / (0.1 * (1 + 1.0 / (span + 1))) * baseAmplitude
		}

	/** The size of the features of octave [index], in blocks. */
	fun wavelength(index: Int) = 2.0.pow(-(baseOctave + index))

	fun json(): String {
		val fields = buildList {
			if (!allOnes) add("\"amplitude_modifiers\": [\n${modifiers.joinToString(",\n") { "\t\t${it.asDouble()}" }}\n\t]")
			if (baseAmplitude != 1.0) add("\"base_amplitude\": ${baseAmplitude.asDouble()}")
			add("\"base_octave\": $baseOctave")
			if (!allOnes || modifiers.size > 1) add("\"octave_count\": ${modifiers.size}")
		}
		return fields.joinToString(",\n", "{\n", "\n}") { "\t$it" }
	}

	fun kotlin(name: String): String {
		val lines = buildList {
			add("baseOctave = $baseOctave")
			when {
				!allOnes -> add("amplitudeModifiers(${modifiers.joinToString { it.asDouble() }})")
				modifiers.size > 1 -> add("octaveCount = ${modifiers.size}")
			}
			if (baseAmplitude != 1.0) add("baseAmplitude = ${baseAmplitude.asDouble()}")
		}
		return lines.joinToString("\n", "val ${name.camelCase()} = dp.noise(\"$name\") {\n", "\n}") { "\t$it" }
	}
}

private class NoisePreset(val name: String, val config: NoiseConfig, val note: String)

private val NOISE_PRESETS = listOf(
	NoisePreset(
		"temperature",
		NoiseConfig(-10, listOf(1.5, 0.0, 1.0, 0.0, 0.0, 0.0), 1.2453007926713473),
		"read by the overworld/temperature density function at xz_scale 0.25.",
	),
	NoisePreset(
		"vegetation",
		NoiseConfig(-8, listOf(1.0, 1.0, 0.0, 0.0, 0.0, 0.0), 0.9494731054427978),
		"read by the overworld/vegetation density function at xz_scale 0.25.",
	),
	NoisePreset(
		"continentalness",
		NoiseConfig(-9, listOf(1.0, 1.0, 2.0, 2.0, 2.0, 1.0, 1.0, 1.0, 1.0), 0.8880832896205223),
		"read by the overworld/continents density function at xz_scale 0.25.",
	),
	NoisePreset(
		"erosion",
		NoiseConfig(-9, listOf(1.0, 1.0, 0.0, 1.0, 1.0), 1.063180125160734),
		"read by the overworld/erosion density function at xz_scale 0.25.",
	),
	NoisePreset(
		"ridge",
		NoiseConfig(-7, listOf(1.0, 2.0, 1.0, 0.0, 0.0, 0.0), 0.9147152149950137),
		"read by the overworld/ridges density function at xz_scale 0.25.",
	),
	NoisePreset(
		"cave_entrance",
		NoiseConfig(-7, listOf(0.4, 0.5, 1.0), 0.8500634887071167),
		"read by the overworld/caves/entrances density function at xz_scale 0.75 and y_scale 0.5.",
	),
	NoisePreset(
		"offset",
		NoiseConfig(-3, listOf(1.0, 1.0, 1.0, 0.0), 0.9381732587751005),
		"read by the shift_x and shift_z density functions, which sample it at the coordinates divided by 4.",
	),
	NoisePreset(
		"surface",
		NoiseConfig(-6, List(3) { 1.0 }, 0.9381732587751008),
		"read by the noise_threshold conditions of material_rule/overworld/surface.json.",
	),
	NoisePreset(
		"jagged",
		NoiseConfig(-16, List(16) { 1.0 }, 1.0383104856073737),
		"read by the overworld/sloped_cheese density function at xz_scale 1500 and y_scale 0.",
	),
)

private enum class NoiseBlock(val label: String, rgb: Int, alpha: Int = 255) {
	AIR("air", 0, 0),
	DIRT("dirt", 0x866043),
	GRASS("grass", 0x5f9f35),
	SAND("sand", 0xdbcfa3),
	STONE("stone", 0x7f7f7f),
	WATER("water", 0x3f76e4, 220);

	val argb = (alpha shl 24) or rgb
}

/** [MAP_PX] squared noise [values] sampled at cell centers on the plane `y = 0`, and their gray [colors]. */
private class NoiseMap(val values: DoubleArray) {
	val colors = IntArray(values.size) {
		val gray = ((values[it].coerceIn(-1.0, 1.0) + 1) / 2 * 255).toInt()
		argb(255, gray, gray, gray)
	}
}

/** A 512 by 128 blocks slice at `z = 0`, in 2-block cells, made solid where the noise plus a gradient around [SURFACE_Y] is positive. */
private class NoiseTerrain(val densities: DoubleArray, val blocks: Array<NoiseBlock>) {
	val colors = IntArray(blocks.size) { blocks[it].argb }

	companion object {
		fun of(values: DoubleArray): NoiseTerrain {
			val densities = DoubleArray(values.size)
			val blocks = Array(densities.size) { NoiseBlock.AIR }
			for (column in 0 until TERRAIN_COLUMNS) {
				var dirtLeft = 0
				for (row in 0 until TERRAIN_ROWS) {
					val index = row * TERRAIN_COLUMNS + column
					val y = (TERRAIN_ROWS - 1 - row) * 2
					val density = values[index] + (SURFACE_Y - y) / 32.0
					densities[index] = density
					blocks[index] = when {
						density <= 0.0 -> {
							dirtLeft = 0
							if (y < SEA_LEVEL) NoiseBlock.WATER else NoiseBlock.AIR
						}

						row == 0 || densities[index - TERRAIN_COLUMNS] <= 0.0 -> {
							dirtLeft = if (y >= SEA_LEVEL) 2 else 0
							if (y >= SEA_LEVEL) NoiseBlock.GRASS else NoiseBlock.SAND
						}

						dirtLeft > 0 -> {
							dirtLeft--
							NoiseBlock.DIRT
						}

						else -> NoiseBlock.STONE
					}
				}
			}
			return NoiseTerrain(densities, blocks)
		}
	}
}

/** Samples a noise with the game's algorithm as a map, a terrain slice or one curve per octave, next to the matching Kotlin and JSON. */
@Composable
fun NoiseExplorer() {
	Style(NoiseExplorerStyle)

	var config by remember { mutableStateOf(NoiseConfig(-7, listOf(1.0, 0.5, 0.25), 1.0)) }
	var view by remember { mutableStateOf(NoiseView.MAP) }
	var area by remember { mutableStateOf(AREAS.first()) }
	var seed by remember { mutableStateOf(0) }
	var preset by remember { mutableStateOf<NoisePreset?>(null) }
	val sampler = remember { NoiseSampler() }
	val kotlin = config.kotlin(preset?.name ?: "my_noise")
	val json = config.json()

	Div({ classes(NoiseExplorerStyle.explorer) }) {
		NoisePresetRow(preset, preset?.let { it.config != config } == true) { chosen ->
			if (chosen == preset) {
				preset = null
				return@NoisePresetRow
			}
			preset = chosen
			config = chosen.config
			area = AREAS.firstOrNull { it >= 2 * chosen.config.wavelength(0) } ?: AREAS.last()
		}

		Div({ classes(NoiseExplorerStyle.toolbar) }) {
			PillGroup("View", NoiseView.entries, view, { it.label }) { view = it }
			if (view != NoiseView.TERRAIN) PillGroup("Area", AREAS, area, { "$it blocks" }) { area = it }
		}

		Div({ classes(NoiseExplorerStyle.body) }) {
			Div({ classes(DocControlsStyle.panel, NoiseExplorerStyle.view) }) {
				when (view) {
					NoiseView.MAP -> NoiseMapView(sampler, seed, config, area)
					NoiseView.OCTAVES -> NoiseOctavesView(sampler, seed, config, area)
					NoiseView.TERRAIN -> NoiseTerrainView(sampler, seed, config)
				}
			}

			Div({ classes(NoiseExplorerStyle.controls) }) {
				SliderField("noise-explorer-base-octave", "baseOctave", config.baseOctave.toDouble(), -16.0, 0.0, 1.0) {
					config = config.copy(baseOctave = it.snapTo(1).coerceIn(-16.0, 0.0).toInt())
				}
				SliderField("noise-explorer-base-amplitude", "baseAmplitude", config.baseAmplitude, 0.0, 2.0, 0.01) {
					config = config.copy(baseAmplitude = it.snapTo(100).coerceIn(0.0, 2.0))
				}
				config.modifiers.forEachIndexed { index, modifier ->
					SliderField(
						"noise-explorer-octave-$index",
						"octave ${config.baseOctave + index} · ${config.wavelength(index).asBlocks()}",
						modifier,
						0.0,
						2.0,
						0.05,
					) { value ->
						val snapped = value.snapTo(20).coerceIn(0.0, 2.0)
						config = config.copy(modifiers = config.modifiers.mapIndexed { other, current -> if (other == index) snapped else current })
					}
				}
				Div({ classes(DocControlsStyle.group) }) {
					NoiseAction("Add octave", config.modifiers.size < MAX_OCTAVES) { config = config.copy(modifiers = config.modifiers + 0.5) }
					NoiseAction("Remove octave", config.modifiers.size > 1) { config = config.copy(modifiers = config.modifiers.dropLast(1)) }
					Pill("New seed") { seed++ }
				}
			}
		}

		Div({ classes(NoiseExplorerStyle.caption) }) { Text(DISCLAIMER) }

		Tabs(
			listOf(
				Tab("Kotlin") { Div({ id(NOISE_KOTLIN_ID) }) { key(kotlin) { CodeBlock(kotlin, "kotlin") } } },
				Tab("JSON") { Div({ id(NOISE_JSON_ID) }) { key(json) { CodeBlock(json, "json") } } },
			),
		)
		highlightCodeIn(NOISE_KOTLIN_ID, kotlin)
		highlightCodeIn(NOISE_JSON_ID, json)
	}
}

@Composable
private fun NoisePresetRow(selected: NoisePreset?, edited: Boolean, onSelect: (NoisePreset) -> Unit) {
	Div({ classes(NoiseExplorerStyle.presets) }) {
		Span({ classes(NoiseExplorerStyle.presetsLabel) }) { Text("Try a vanilla noise") }
		PillGroup("Vanilla noises", NOISE_PRESETS, selected, NoisePreset::name, onSelect)
		selected?.let {
			Div({ classes(NoiseExplorerStyle.caption) }) {
				Text("from ")
				Span({ classes(NoiseExplorerStyle.captionFile) }) { Text("worldgen/noise/${it.name}.json") }
				Text(": ${it.note}${if (edited) " (edited)" else ""}")
			}
		}
	}
}

/** A [Pill] that does nothing and looks off while it is not [enabled]. */
@Composable
private fun NoiseAction(text: String, enabled: Boolean, onClick: () -> Unit) {
	Span({ if (!enabled) classes(NoiseExplorerStyle.actionOff) }) {
		Pill(text) { if (enabled) onClick() }
	}
}

/** A canvas of [columns] by [rows] cells painted from [colors] (ARGB), reporting the cell under the mouse. */
@Composable
private fun NoiseGridCanvas(
	columns: Int,
	rows: Int,
	colors: IntArray,
	label: String,
	extraClass: String,
	onHover: (Int, Int) -> Unit,
	onLeave: () -> Unit,
) {
	Canvas({
		classes(NoiseExplorerStyle.canvas, extraClass)
		attr("aria-label", label)
		attr("height", rows.toString())
		attr("role", "img")
		attr("width", columns.toString())
		onMouseMove { event ->
			val target = event.target as HTMLElement
			val column = (event.offsetX / target.clientWidth * columns).toInt().coerceIn(0, columns - 1)
			val row = (event.offsetY / target.clientHeight * rows).toInt().coerceIn(0, rows - 1)
			onHover(column, row)
		}
		onMouseLeave { onLeave() }
	}) {
		DisposableEffect(colors) {
			val context = scopeElement.getContext("2d") as CanvasRenderingContext2D
			val image = context.createImageData(columns.toDouble(), rows.toDouble())
			val bytes = Uint8Array(image.data.buffer, 0, image.data.length)
			colors.forEachIndexed { index, color ->
				bytes[index * 4] = (color shr 16).toByte()
				bytes[index * 4 + 1] = (color shr 8).toByte()
				bytes[index * 4 + 2] = color.toByte()
				bytes[index * 4 + 3] = (color ushr 24).toByte()
			}
			context.putImageData(image, 0.0, 0.0)
			onDispose {}
		}
	}
}

@Composable
private fun NoiseMapView(sampler: NoiseSampler, seed: Int, config: NoiseConfig, area: Int) {
	var hovered by remember { mutableStateOf<Int?>(null) }
	val map = remember(config, seed, area) { NoiseMap(sampler.value(NoiseGrid(NoiseView.MAP, area), seed, config)) }
	val wavelength = config.wavelength(0)

	NoiseGridCanvas(
		MAP_PX,
		MAP_PX,
		map.colors,
		"Noise map of $area by $area blocks",
		NoiseExplorerStyle.canvasMap,
		{ column, row -> hovered = row * MAP_PX + column },
	) { hovered = null }
	Div({ classes(NoiseExplorerStyle.readout) }) {
		Text(
			hovered?.let {
				"x ${((it % MAP_PX + 0.5) * area / MAP_PX).toInt()}, z ${((it / MAP_PX + 0.5) * area / MAP_PX).toInt()}: ${map.values[it].readout()}"
			} ?: "Hover the map to read a value",
		)
	}
	Div({ classes(NoiseExplorerStyle.ruler) }) {
		Div({
			classes(NoiseExplorerStyle.rulerBar)
			style { width((min(wavelength, area.toDouble()) / area * 100).percent) }
		})
		Span({ classes(NoiseExplorerStyle.readout) }) { Text("${wavelength.asBlocks()}, base octave") }
	}
}

@Composable
private fun NoiseTerrainView(sampler: NoiseSampler, seed: Int, config: NoiseConfig) {
	var hovered by remember { mutableStateOf<Int?>(null) }
	val terrain = remember(config, seed) { NoiseTerrain.of(sampler.value(NoiseGrid(NoiseView.TERRAIN, 0), seed, config)) }

	NoiseGridCanvas(
		TERRAIN_COLUMNS,
		TERRAIN_ROWS,
		terrain.colors,
		"Terrain slice of ${TERRAIN_COLUMNS * 2} by ${TERRAIN_ROWS * 2} blocks",
		NoiseExplorerStyle.canvasTerrain,
		{ column, row -> hovered = row * TERRAIN_COLUMNS + column },
	) { hovered = null }
	Div({ classes(NoiseExplorerStyle.readout) }) {
		Text(
			hovered?.let {
				"x ${it % TERRAIN_COLUMNS * 2}, y ${(TERRAIN_ROWS - 1 - it / TERRAIN_COLUMNS) * 2}: " +
					"density ${terrain.densities[it].readout()} (${terrain.blocks[it].label})"
			} ?: "Hover the terrain to read a density",
		)
	}
	Div({ classes(NoiseExplorerStyle.legend) }) {
		Span { McSprite("block/stone", "Stone"); Text("stone") }
		Span { McSprite("block/dirt", "Dirt"); Text("dirt") }
		Span { McSprite("block/grass_block_top", "Grass", Color("#91bd59")); Text("grass") }
		Span { McSprite("block/sand", "Sand"); Text("sand") }
		Span { McSprite("block/water_still", "Water", Color("#3f76e4")); Text("water") }
	}
	Div({ classes(NoiseExplorerStyle.caption) }) {
		Text("density = noise(x, y, 0) + ($SURFACE_Y - y) / 32, sea level $SEA_LEVEL: positive is stone, negative is air or water")
	}
}

@Composable
private fun NoiseOctavesView(sampler: NoiseSampler, seed: Int, config: NoiseConfig, area: Int) {
	val grid = NoiseGrid(NoiseView.OCTAVES, area)
	val z = octavesZ(area)

	config.modifiers.forEachIndexed { index, modifier ->
		val skipped = modifier == 0.0
		val svg = remember(config, seed, area) { noiseCurve(sampler.octave(grid, seed, config, index)) }
		val label = "octave ${config.baseOctave + index} · ${config.wavelength(index).asBlocks()}"
		NoiseOctaveRow(if (skipped) "$label · skipped, modifier 0" else "$label · ${modifier.asDouble()}", skipped, false, svg)
	}
	val sum = remember(config, seed, area) { noiseCurve(sampler.value(grid, seed, config)) }
	NoiseOctaveRow("sum at z ${z.toInt()}", false, true, sum)
}

@Composable
private fun NoiseOctaveRow(label: String, skipped: Boolean, isSum: Boolean, svg: String) {
	Div({ classes(NoiseExplorerStyle.octaveRow) }) {
		Span({ classes(if (skipped) NoiseExplorerStyle.octaveSkipped else NoiseExplorerStyle.octaveLabel) }) { Text(label) }
		Div({
			classes(NoiseExplorerStyle.octavePlot)
			if (isSum) classes(NoiseExplorerStyle.octavePlotSum)
		}) {
			DisposableEffect(svg) {
				scopeElement.innerHTML = svg
				onDispose {}
			}
		}
	}
}

/** An SVG polyline of the [OCTAVE_SAMPLES] [values] of an Octaves grid, on a fixed -1..1 scale. */
private fun noiseCurve(values: DoubleArray): String {
	val points = (0 until OCTAVE_SAMPLES).joinToString(" ") { sample ->
		val y = (1 - values[sample].coerceIn(-1.0, 1.0)) * 50
		"$sample,${round(y * 10) / 10 + 0.0}"
	}
	return """<svg viewBox="0 0 256 100" preserveAspectRatio="none" width="100%" height="100%">""" +
		"""<line x1="0" y1="50" x2="256" y2="50" stroke="currentColor" stroke-opacity="0.3" vector-effect="non-scaling-stroke"/>""" +
		"""<polyline fill="none" stroke="currentColor" vector-effect="non-scaling-stroke" points="$points"/></svg>"""
}

private object NoiseExplorerStyle : StyleSheet() {
	val actionOff by style {
		opacity(0.4)
		pointerEvents(PointerEvents.None)
	}

	val body by style {
		alignItems(AlignItems.FlexStart)
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(1.cssRem)
	}

	val canvas by style {
		backgroundColor(LandingVars.Surface.value())
		cursor(Cursor.Crosshair)
		display(DisplayStyle.Block)
		imageRendering(ImageRendering.Pixelated)
		width(100.percent)
	}

	val canvasMap by style {
		aspectRatio(AspectRatio.of(1, 1))
	}

	val canvasTerrain by style {
		aspectRatio(AspectRatio.of(4, 1))
	}

	val caption by style {
		color(LandingVars.Muted.value())
		fontSize(0.8.cssRem)
	}

	val captionFile by style {
		color(LandingVars.Text.value())
		monoFont()
	}

	val controls by style {
		display(DisplayStyle.Flex)
		flexBasis(18.cssRem)
		flexDirection(FlexDirection.Column)
		flexGrow(1)
		flexShrink(1)
		gap(0.9.cssRem)
		minWidth(0.px)
	}

	val explorer by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(1.cssRem)
		margin(1.5.cssRem, 0.px)
		minWidth(0.px)
		width(100.percent)
	}

	val legend by style {
		alignItems(AlignItems.Center)
		color(LandingVars.Muted.value())
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		fontSize(0.75.cssRem)
		gap(0.4.cssRem, 1.cssRem)
		monoFont()
	}

	val octaveLabel by style {
		color(LandingVars.Text.value())
		fontSize(0.75.cssRem)
		minWidth(0.px)
		monoFont()
	}

	val octavePlot by style {
		color(LandingVars.Accent.value())
		height(1.8.cssRem)
		minWidth(0.px)

		"svg" style {
			display(DisplayStyle.Block)
		}
	}

	val octavePlotSum by style {
		color(LandingVars.Text.value())
	}

	val octaveRow by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Grid)
		gap(0.7.cssRem)
		gridTemplateColumns("minmax(0, 9rem) minmax(0, 1fr)")
	}

	val octaveSkipped by style {
		color(LandingVars.Muted.value())
		fontSize(0.75.cssRem)
		minWidth(0.px)
		monoFont()
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

	val readout by style {
		color(LandingVars.Text.value())
		fontSize(0.8.cssRem)
		monoFont()
	}

	val ruler by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.35.cssRem)
	}

	val rulerBar by style {
		backgroundColor(LandingVars.Text.value())
		height(0.3.cssRem)
		minWidth(2.px)
	}

	val toolbar by style {
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(0.6.cssRem, 1.2.cssRem)
	}

	val view by style {
		boxSizing(BoxSizing.BorderBox)
		display(DisplayStyle.Flex)
		flexBasis(24.cssRem)
		flexDirection(FlexDirection.Column)
		flexGrow(1)
		flexShrink(1)
		gap(0.7.cssRem)
	}
}
