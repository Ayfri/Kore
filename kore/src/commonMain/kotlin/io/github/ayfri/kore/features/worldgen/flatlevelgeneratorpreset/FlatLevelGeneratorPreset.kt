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
 * One entry of the superflat customization screen: an icon and the superflat settings it applies.
 *
 * A preset only shows up in the screen when it is listed in the `minecraft:visible` flat level generator preset tag.
 *
 * The defaults reproduce the vanilla `classic_flat` preset: a grass block icon, the plains biome, a bedrock, dirt and
 * grass block stack, and villages as the only structure set.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/world-presets
 * Minecraft Wiki: https://minecraft.wiki/w/World_preset_definition#Superflat_Level_Generation_Preset
 *
 * @property display The item used as the icon of the preset, which has to be an existing item.
 * @property settings The superflat settings the preset applies.
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
 * Creates a flat level generator preset shown with the [display] icon, configured in [init].
 *
 * Everything left untouched keeps the vanilla `classic_flat` values, so a preset only has to declare what it changes.
 *
 * ```kotlin
 * flatLevelGeneratorPreset("tunnelers_dream", Items.STONE) {
 *     settings {
 *         biome = Biomes.WINDSWEPT_HILLS
 *         layers {
 *             layer(Blocks.BEDROCK)
 *             layer(Blocks.STONE, height = 230)
 *             layer(Blocks.DIRT, height = 5)
 *             layer(Blocks.GRASS_BLOCK)
 *         }
 *         structureOverrides(StructureSets.MINESHAFTS, StructureSets.STRONGHOLDS)
 *     }
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/flat_level_generator_preset/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/world-presets
 * Minecraft Wiki: https://minecraft.wiki/w/World_preset_definition#Superflat_Level_Generation_Preset
 */
fun DataPack.flatLevelGeneratorPreset(
	fileName: String = "flat_level_generator_preset",
	display: ItemArgument = Items.GRASS_BLOCK,
	init: FlatLevelGeneratorPreset.() -> Unit = {},
): FlatLevelGeneratorPresetArgument {
	val preset = FlatLevelGeneratorPreset(fileName, display).apply(init)
	flatLevelGeneratorPresets += preset
	return FlatLevelGeneratorPresetArgument(fileName, preset.namespace ?: name)
}

/**
 * Configures the superflat settings of the preset, starting from the `classic_flat` values.
 *
 * ```kotlin
 * flatLevelGeneratorPreset("water_world", Items.WATER_BUCKET) {
 *     settings {
 *         biome = Biomes.DEEP_OCEAN
 *         layers {
 *             layer(Blocks.BEDROCK, height = 5)
 *             layer(Blocks.DEEPSLATE, height = 5)
 *             layer(Blocks.WATER, height = 90)
 *         }
 *     }
 * }
 * ```
 */
fun FlatLevelGeneratorPreset.settings(block: FlatGeneratorSettings.() -> Unit) = settings.apply(block)
