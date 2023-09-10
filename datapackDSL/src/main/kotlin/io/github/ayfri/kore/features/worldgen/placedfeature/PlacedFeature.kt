package io.github.ayfri.kore.features.worldgen.placedfeature

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.arguments.types.resources.worldgen.ConfiguredFeatureArgument
import io.github.ayfri.kore.arguments.types.resources.worldgen.PlacedFeatureArgument
import io.github.ayfri.kore.features.worldgen.placedfeature.modifiers.PlacementModifier
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString

@Serializable
data class PlacedFeature(
	@Transient
	override var fileName: String = "placed_feature",
	var feature: ConfiguredFeatureArgument,
	@SerialName("placement")
	var placementModifiers: List<PlacementModifier> = emptyList(),
) : Generator("worldgen/placed_feature") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

fun DataPack.placedFeature(
	fileName: String = "placed_feature",
	feature: ConfiguredFeatureArgument,
	block: PlacedFeature.() -> Unit,
): PlacedFeatureArgument {
	placedFeatures += PlacedFeature(fileName, feature).apply(block)
	return PlacedFeatureArgument(fileName, name)
}
