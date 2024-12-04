package io.github.ayfri.kore.features.worldgen.noisesettings

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.arguments.types.resources.worldgen.NoiseSettingsArgument
import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.data.block.blockState
import io.github.ayfri.kore.features.worldgen.dimension.biomesource.multinoise.MultiNoiseBiomeSourceParameters
import io.github.ayfri.kore.features.worldgen.noisesettings.rules.Bandlands
import io.github.ayfri.kore.features.worldgen.noisesettings.rules.Block
import io.github.ayfri.kore.features.worldgen.noisesettings.rules.SurfaceRule
import io.github.ayfri.kore.generated.Blocks
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString

/**
 * Represents the settings for generating noise in a world.
 *
 * @property fileName The name of the file where the noise settings will be saved.
 * @property seaLevel The sea level of the world.
 * @property disableMobGeneration Specifies whether mob generation is disabled.
 * @property aquifersEnabled Specifies whether aquifers are enabled.
 * @property oreVeinsEnabled Specifies whether ore veins are enabled.
 * @property legacyRandomSource Specifies whether the legacy random source is used.
 * @property defaultBlock The default block state used in the noise generation.
 * @property defaultFluid The default fluid block state used in the noise generation.
 * @property noise The options for generating noise.
 * @property noiseRouter The noise router used for generating noise.
 * @property spawnTarget The list of multi-noise biome source parameters.
 * @property surfaceRule The surface rule used in the noise generation.
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
 * Creates a new instance of [NoiseSettings] with default options.
 */
fun DataPack.noiseSettings(fileName: String = "noise_settings", init: NoiseSettings.() -> Unit = {}): NoiseSettingsArgument {
	val settings = NoiseSettings(fileName).apply(init)
	noiseSettings += settings
	return NoiseSettingsArgument(fileName, settings.namespace ?: name)
}

/**
 * Routes the noise settings using the provided configuration.
 */
fun NoiseSettings.noiseRouter(block: NoiseRouter.() -> Unit = {}) = NoiseRouter().apply(block).also { noiseRouter = it }

/**
 * Initializes the noise options for the given noise settings.
 *
 * @param minY The minimum Y coordinate for generating noise.
 * @param height The height of the noise region.
 * @param sizeHorizontal The horizontal size of the noise region.
 * @param sizeVertical The vertical size of the noise region.
 */
fun NoiseSettings.noiseOptions(
	minY: Int,
	height: Int,
	sizeHorizontal: Int,
	sizeVertical: Int,
) = NoiseOptions(minY, height, sizeHorizontal, sizeVertical).also { noise = it }

/**
 * Sets the default block state for this [NoiseSettings].
 */
fun NoiseSettings.defaultBlock(block: BlockArgument, properties: MutableMap<String, String>.() -> Unit = {}) =
	blockState(block, buildMap(properties)).also { defaultBlock = it }

/**
 * Sets the default fluid block state for this [NoiseSettings].
 */
fun NoiseSettings.defaultFluid(fluid: BlockArgument, properties: MutableMap<String, String>.() -> Unit = {}) =
	blockState(fluid, buildMap(properties)).also { defaultFluid = it }
