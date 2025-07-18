package io.github.ayfri.kore.features.worldgen.worldpreset

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.features.worldgen.dimension.Dimension
import io.github.ayfri.kore.generated.DimensionTypes
import io.github.ayfri.kore.generated.arguments.worldgen.types.WorldPresetArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

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
