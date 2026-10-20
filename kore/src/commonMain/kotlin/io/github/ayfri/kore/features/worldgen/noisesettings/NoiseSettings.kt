package io.github.ayfri.kore.features.worldgen.noisesettings

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.data.block.blockState
import io.github.ayfri.kore.features.worldgen.dimension.biomesource.multinoise.DoubleOrDoublePair
import io.github.ayfri.kore.features.worldgen.dimension.biomesource.multinoise.doubleOrPair
import io.github.ayfri.kore.features.worldgen.noisesettings.rules.Bandlands
import io.github.ayfri.kore.features.worldgen.noisesettings.rules.MaterialRule
import io.github.ayfri.kore.generated.Blocks
import io.github.ayfri.kore.generated.arguments.worldgen.types.DensityFunctionArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.NoiseSettingsArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Data-driven noise settings controlling terrain generation.
 *
 * Defines the vertical range, sea level, default terrain/fluid states, whether aquifers and ore
 * veins are enabled, the noise router graph, spawn targets and material rules. Referenced by the
 * noise world generator.
 *
 * JSON format reference: https://minecraft.wiki/w/Noise_settings
 *
 * @property spawnTarget The points of climate space the world spawn prefers, each mapping density functions to the value or
 * `[min, max]` range wanted there, see [spawnTargetPoint]. Candidates are scored by how far their climate is outside the
 * closest point (lower is better) plus their distance from `0, 0`, so it is a preference, not a filter.
 */
@Serializable
data class NoiseSettings(
	@Transient
	override var fileName: String = "noise_settings",
	var seaLevel: Int = 63,
	var disableMobGeneration: Boolean = false,
	var aquifersEnabled: Boolean = false,
	var oreVeinsEnabled: Boolean = false,
	var legacyRandomSource: Boolean = false,
	var defaultBlock: BlockState = blockState(Blocks.STONE),
	var defaultFluid: BlockState = blockState(Blocks.WATER, mapOf("level" to "0")),
	var noise: NoiseOptions = NoiseOptions(-64, 384, 1, 2),
	var noiseRouter: NoiseRouter = NoiseRouter(),
	var spawnTarget: List<Map<DensityFunctionArgument, DoubleOrDoublePair>> = emptyList(),
	var materialRule: MaterialRule = Bandlands,
) : Generator("worldgen/noise_settings") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

/**
 * Creates a noise settings file with default options using a builder block.
 *
 * Produces `data/<namespace>/worldgen/noise_settings/<fileName>.json`.
 *
 * JSON format reference: https://minecraft.wiki/w/Noise_settings
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 */
fun DataPack.noiseSettings(fileName: String = "noise_settings", init: NoiseSettings.() -> Unit = {}): NoiseSettingsArgument {
	val settings = NoiseSettings(fileName).apply(init)
	noiseSettings += settings
	return NoiseSettingsArgument(fileName, settings.namespace ?: name)
}

/** Configure the noise router subgraph via a builder. */
fun NoiseSettings.noiseRouter(block: NoiseRouter.() -> Unit = {}) = NoiseRouter().apply(block).also { noiseRouter = it }

/** Set core noise options (vertical range and sampling resolution). */
fun NoiseSettings.noiseOptions(
	minY: Int,
	height: Int,
	sizeHorizontal: Int,
	sizeVertical: Int,
) = NoiseOptions(minY, height, sizeHorizontal, sizeVertical).also { noise = it }

/**
 * Appends a [NoiseSettings.spawnTarget] point filled in [init]: the world spawn prefers where every given density
 * function is inside its value or `min..max` range. Candidates are scored by how far their climate is outside the closest
 * point (lower is better) plus their distance from `0, 0`, so a point is a preference, not a filter.
 *
 * ```kotlin
 * spawnTargetPoint {
 *     this[DensityFunctions.Overworld.CONTINENTS] = -0.11..1.0
 *     this[DensityFunctions.Overworld.RIDGES] = 0.0
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/noise
 */
fun NoiseSettings.spawnTargetPoint(init: MutableMap<DensityFunctionArgument, DoubleOrDoublePair>.() -> Unit) {
	spawnTarget += buildMap(init)
}

/** Requires the density function [key] to equal [value] at the spawn. */
operator fun MutableMap<DensityFunctionArgument, DoubleOrDoublePair>.set(key: DensityFunctionArgument, value: Double) {
	this[key] = doubleOrPair(value)
}

/** Requires the density function [key] to be inside [range] at the spawn. */
operator fun MutableMap<DensityFunctionArgument, DoubleOrDoublePair>.set(
	key: DensityFunctionArgument,
	range: ClosedFloatingPointRange<Double>,
) {
	this[key] = doubleOrPair(range)
}

/** Sets the default block state for this [NoiseSettings]. */
fun NoiseSettings.defaultBlock(block: BlockArgument, properties: MutableMap<String, String>.() -> Unit = {}) =
	blockState(block, buildMap(properties)).also { defaultBlock = it }

/** Sets the default fluid block state for this [NoiseSettings]. */
fun NoiseSettings.defaultFluid(fluid: BlockArgument, properties: MutableMap<String, String>.() -> Unit = {}) =
	blockState(fluid, buildMap(properties)).also { defaultFluid = it }
