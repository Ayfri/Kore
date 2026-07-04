package io.github.ayfri.kore.features.worldgen.noisesettings

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.data.block.blockState
import io.github.ayfri.kore.features.worldgen.dimension.biomesource.multinoise.MultiNoiseBiomeSourceParameters
import io.github.ayfri.kore.features.worldgen.noisesettings.rules.Bandlands
import io.github.ayfri.kore.features.worldgen.noisesettings.rules.Block
import io.github.ayfri.kore.features.worldgen.noisesettings.rules.SurfaceRule
import io.github.ayfri.kore.generated.Blocks
import io.github.ayfri.kore.generated.arguments.worldgen.types.NoiseSettingsArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Data-driven noise settings controlling terrain generation.
 *
 * Defines the vertical range, sea level, default terrain/fluid states, whether aquifers and ore
 * veins are enabled, the noise router graph, spawn targets and surface rules. Referenced by the
 * noise world generator.
 *
 * JSON format reference: https://minecraft.wiki/w/Noise_settings
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
	@Serializable(with = Block.Companion.BlockStateSerializer::class)
	var defaultBlock: BlockState = blockState(Blocks.STONE),
	@Serializable(with = Block.Companion.BlockStateSerializer::class)
	var defaultFluid: BlockState = blockState(Blocks.WATER, mapOf("level" to "0")),
	var noise: NoiseOptions = NoiseOptions(-64, 384, 1, 2),
	var noiseRouter: NoiseRouter = NoiseRouter(),
	var spawnTarget: List<MultiNoiseBiomeSourceParameters> = emptyList(),
	var surfaceRule: SurfaceRule = Bandlands,
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

/** Sets the default block state for this [NoiseSettings]. */
fun NoiseSettings.defaultBlock(block: BlockArgument, properties: MutableMap<String, String>.() -> Unit = {}) =
	blockState(block, buildMap(properties)).also { defaultBlock = it }

/** Sets the default fluid block state for this [NoiseSettings]. */
fun NoiseSettings.defaultFluid(fluid: BlockArgument, properties: MutableMap<String, String>.() -> Unit = {}) =
	blockState(fluid, buildMap(properties)).also { defaultFluid = it }
