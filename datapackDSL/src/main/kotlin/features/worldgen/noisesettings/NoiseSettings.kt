package features.worldgen.noisesettings

import DataPack
import Generator
import arguments.Argument
import data.block.BlockState
import data.block.blockState
import features.worldgen.dimension.biomesource.multinoise.MultiNoiseBiomeSourceParameters
import features.worldgen.noisesettings.rules.Bandlands
import features.worldgen.noisesettings.rules.Block
import features.worldgen.noisesettings.rules.SurfaceRule
import generated.Blocks
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString

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
) : Generator {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

/**
 * Creates a new [NoiseSettings], default options are from overworld except for the [NoiseSettings.noiseRouter],
[NoiseSettings.spawnTarget] and [NoiseSettings.surfaceRule] which are empty for simpler instantiation.
 */
fun DataPack.noiseSettings(fileName: String = "noise_settings", configure: NoiseSettings.() -> Unit = {}): Argument.NoiseSettings {
	noiseSettings += NoiseSettings(fileName).apply(configure)
	return Argument.NoiseSettings(fileName, name)
}

fun NoiseSettings.noiseRouter(configure: NoiseRouter.() -> Unit = {}) = NoiseRouter().apply(configure).also { noiseRouter = it }

fun NoiseSettings.noiseOptions(
	minY: Int,
	height: Int,
	sizeHorizontal: Int,
	sizeVertical: Int
) = NoiseOptions(minY, height, sizeHorizontal, sizeVertical).also { noise = it }

fun NoiseSettings.defaultBlock(block: Argument.Block, properties: MutableMap<String, String>.() -> Unit = {}) =
	blockState(block, buildMap(properties)).also { defaultBlock = it }

fun NoiseSettings.defaultFluid(fluid: Argument.Block, properties: MutableMap<String, String>.() -> Unit = {}) =
	blockState(fluid, buildMap(properties)).also { defaultFluid = it }
