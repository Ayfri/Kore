package features.worldgen.worldpreset

import DataPack
import Generator
import arguments.types.resources.worldgen.WorldPresetArgument
import features.worldgen.dimension.Dimension
import generated.DimensionTypes
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString

@Serializable
data class WorldPreset(
	@Transient
	override var fileName: String = "world_preset",
	var dimensions: Map<DimensionTypes, Dimension> = emptyMap(),
) : Generator("worldgen/world_preset") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

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
