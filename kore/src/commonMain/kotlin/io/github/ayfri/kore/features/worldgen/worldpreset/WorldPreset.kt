package io.github.ayfri.kore.features.worldgen.worldpreset

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.features.worldgen.dimension.Dimension
import io.github.ayfri.kore.generated.arguments.types.DimensionArgument
import io.github.ayfri.kore.generated.arguments.types.DimensionTypeArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.WorldPresetArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * One entry of the world type dropdown of the world creation screen: the set of dimensions a new world is built with.
 *
 * A preset only shows up in the dropdown when it is listed in the `minecraft:normal` world preset tag, and a world
 * needs at least a `minecraft:overworld` dimension to be playable.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/world-presets
 * Minecraft Wiki: https://minecraft.wiki/w/World_preset_definition
 *
 * @property dimensions The dimensions of the world, keyed by their id, each one holding its type and its generator.
 */
@Serializable
data class WorldPreset(
	@Transient
	override var fileName: String = "world_preset",
	var dimensions: Map<DimensionArgument, Dimension> = emptyMap(),
) : Generator("worldgen/world_preset") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

/**
 * Creates a world preset, its dimensions declared in [block] through [dimension].
 *
 * ```kotlin
 * worldPreset("my_preset") {
 *     dimension(Dimensions.OVERWORLD, DimensionTypes.OVERWORLD) {
 *         noiseGenerator(NoiseSettings.OVERWORLD, multiNoise(BiomePresets.OVERWORLD))
 *     }
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/world_preset/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/world-presets
 * Minecraft Wiki: https://minecraft.wiki/w/World_preset_definition
 */
fun DataPack.worldPreset(
	fileName: String = "world_preset",
	block: WorldPreset.() -> Unit = {},
): WorldPresetArgument {
	val worldPreset = WorldPreset(fileName).apply(block)
	worldPresets += worldPreset
	return WorldPresetArgument(fileName, worldPreset.namespace ?: name)
}

/**
 * Adds the dimension [id] of the given [type] to the preset, its generator picked in [block].
 *
 * [id] is the id the world knows the dimension by, `minecraft:overworld` for the dimension players spawn in, while
 * [type] is the dimension type file holding its height bounds, lighting and environment attributes. Both are the
 * same name for the vanilla dimensions, but a preset can put a custom type behind a vanilla id, or add a dimension
 * of its own.
 *
 * ```kotlin
 * worldPreset("aether") {
 *     dimension(Dimensions.OVERWORLD, aetherType) {
 *         noiseGenerator(aetherNoise, fixed(Biomes.PLAINS))
 *     }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/World_preset_definition
 */
fun WorldPreset.dimension(
	id: DimensionArgument,
	type: DimensionTypeArgument,
	block: Dimension.() -> Unit = {},
) {
	dimensions += id to Dimension(type = type).apply(block)
}
