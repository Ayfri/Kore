package io.github.ayfri.kore.features.worldgen.flatlevelgeneratorpreset

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.arguments.types.resources.worldgen.FlatLevelGeneratorPresetArgument
import io.github.ayfri.kore.features.worldgen.dimension.generator.FlatGeneratorSettings
import io.github.ayfri.kore.features.worldgen.dimension.generator.Layer
import io.github.ayfri.kore.generated.Biomes
import io.github.ayfri.kore.generated.Blocks
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.generated.StructureSets
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
) : Generator("worldgen/flat_level_generator_preset") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

/**
 * Generates a [FlatLevelGeneratorPreset] with default values of from classic_flat preset.
 */
fun DataPack.flatLevelGeneratorPreset(
	fileName: String = "flat_level_generator_preset",
	init: FlatLevelGeneratorPreset.() -> Unit = {},
): FlatLevelGeneratorPresetArgument {
	val preset = FlatLevelGeneratorPreset(fileName).apply(init)
	flatLevelGeneratorPresets += preset
	return FlatLevelGeneratorPresetArgument(fileName, preset.namespace ?: name)
}

/**
 * Generates a [FlatLevelGeneratorPreset] with default values of from classic_flat preset.
 */
fun DataPack.flatLevelGeneratorPreset(
	fileName: String = "flat_level_generator_preset",
	display: ItemArgument,
	block: FlatGeneratorSettings.() -> Unit = {},
): FlatLevelGeneratorPresetArgument {
	flatLevelGeneratorPresets += FlatLevelGeneratorPreset(fileName, display).also { it.settings.apply(block) }
	return FlatLevelGeneratorPresetArgument(fileName, name)
}

fun FlatLevelGeneratorPreset.settings(block: FlatGeneratorSettings.() -> Unit) = settings.apply(block)
