package features.worldgen.flatlevelgeneratorpreset

import DataPack
import Generator
import arguments.types.resources.ItemArgument
import arguments.types.resources.worldgen.FlatLevelGeneratorPresetArgument
import features.worldgen.dimension.generator.FlatGeneratorSettings
import features.worldgen.dimension.generator.Layer
import generated.Biomes
import generated.Blocks
import generated.Items
import generated.StructureSets
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString

@Serializable
data class FlatLevelGeneratorPreset(
	@Transient
	override var fileName: String = "flat_level_generator_preset",
	var display: ItemArgument = Items.GRASS_BLOCK,
	var settings: FlatGeneratorSettings = FlatGeneratorSettings(
		biome = Biomes.PLAINS,
		layers = listOf(
			Layer(Blocks.BEDROCK, 1),
			Layer(Blocks.DIRT, 2),
			Layer(Blocks.GRASS_BLOCK, 1),
		),
		structureOverrides = listOf(StructureSets.VILLAGES)
	),
) : Generator {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

/**
 * Generates a [FlatLevelGeneratorPreset] with default values of from classic_flat preset.
 */
fun DataPack.flatLevelGeneratorPreset(
	fileName: String = "flat_level_generator_preset",
	block: FlatLevelGeneratorPreset.() -> Unit
): FlatLevelGeneratorPresetArgument {
	val generatorPreset = FlatLevelGeneratorPreset(fileName).also(block)
	flatLevelGeneratorPresets += generatorPreset
	return FlatLevelGeneratorPresetArgument(fileName, name)
}

/**
 * Generates a [FlatLevelGeneratorPreset] with default values of from classic_flat preset.
 */
fun DataPack.flatLevelGeneratorPreset(
	fileName: String = "flat_level_generator_preset",
	display: ItemArgument,
	block: FlatGeneratorSettings.() -> Unit = {}
): FlatLevelGeneratorPresetArgument {
	val generatorPreset = FlatLevelGeneratorPreset(fileName, display).also { it.settings.apply(block) }
	flatLevelGeneratorPresets += generatorPreset
	return FlatLevelGeneratorPresetArgument(fileName, name)
}

fun FlatLevelGeneratorPreset.settings(block: FlatGeneratorSettings.() -> Unit) = settings.apply(block)
