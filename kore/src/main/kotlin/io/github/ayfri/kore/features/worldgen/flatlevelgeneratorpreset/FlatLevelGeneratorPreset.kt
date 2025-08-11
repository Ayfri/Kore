package io.github.ayfri.kore.features.worldgen.flatlevelgeneratorpreset

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.features.worldgen.dimension.generator.FlatGeneratorSettings
import io.github.ayfri.kore.features.worldgen.dimension.generator.Layer
import io.github.ayfri.kore.generated.Biomes
import io.github.ayfri.kore.generated.Blocks
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.generated.StructureSets
import io.github.ayfri.kore.generated.arguments.worldgen.types.FlatLevelGeneratorPresetArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Data-driven flat level generator preset.
 *
 * Defines the preset shown in the world creation UI for flat worlds: display item, biome,
 * layer stack and structure overrides.
 *
 * JSON format reference: https://minecraft.wiki/w/World_preset_definition#Superflat_Level_Generation_Preset
 */
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
 * Creates a preset using a builder block. Defaults match the classic_flat preset.
 *
 * Produces `data/<namespace>/worldgen/flat_level_generator_preset/<fileName>.json`.
 *
 * JSON format reference: https://minecraft.wiki/w/World_preset_definition#Superflat_Level_Generation_Preset
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
 * Creates a preset with an explicit display item; settings configured in the provided block.
 *
 * Produces `data/<namespace>/worldgen/flat_level_generator_preset/<fileName>.json`.
 *
 * JSON format reference: https://minecraft.wiki/w/World_preset_definition#Superflat_Level_Generation_Preset
 */
fun DataPack.flatLevelGeneratorPreset(
	fileName: String = "flat_level_generator_preset",
	display: ItemArgument,
	block: FlatGeneratorSettings.() -> Unit = {},
): FlatLevelGeneratorPresetArgument {
	flatLevelGeneratorPresets += FlatLevelGeneratorPreset(fileName, display).also { it.settings.apply(block) }
	return FlatLevelGeneratorPresetArgument(fileName, name)
}

/** Configure the settings for the preset. */
fun FlatLevelGeneratorPreset.settings(block: FlatGeneratorSettings.() -> Unit) = settings.apply(block)
