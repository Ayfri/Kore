package io.github.ayfri.kore.features.worldgen.worldpreset

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.features.worldgen.dimension.Dimension
import io.github.ayfri.kore.generated.DimensionTypes
import io.github.ayfri.kore.generated.arguments.worldgen.types.WorldPresetArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Data-driven world preset aggregating dimensions.
 *
 * A world preset defines the set of dimensions and their order for a single world creation option
 * (e.g. Overworld/Nether/End trio), used by world creation UI and presets.
 *
 * JSON format reference: https://minecraft.wiki/w/World_preset_definition
 */
@Serializable
data class WorldPreset(
	@Transient
	override var fileName: String = "world_preset",
	var dimensions: Map<DimensionTypes, Dimension> = emptyMap(),
) : Generator("worldgen/world_preset") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

/**
 * Creates a world preset using a builder block.
 *
 * Use [dimension] or [dimensions] to populate the preset.
 *
 * Produces `data/<namespace>/worldgen/world_preset/<fileName>.json`.
 *
 * JSON format reference: https://minecraft.wiki/w/World_preset_definition
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 */
fun DataPack.worldPreset(
	fileName: String = "world_preset",
	block: WorldPreset.() -> Unit = {},
): WorldPresetArgument {
	worldPresets += WorldPreset(fileName).apply(block)
	return WorldPresetArgument(fileName, name)
}

fun WorldPreset.dimension(
	type: DimensionTypes,
	block: Dimension.() -> Unit = {},
) {
	dimensions += type to Dimension(type = type).apply(block)
}

fun WorldPreset.dimensions(block: MutableMap<DimensionTypes, Dimension>.() -> Unit = {}) {
	dimensions = buildMap(block)
}
