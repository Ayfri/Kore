package features.worldgen.placedfeature

import DataPack
import Generator
import arguments.types.resources.worldgen.ConfiguredFeatureArgument
import arguments.types.resources.worldgen.PlacedFeatureArgument
import features.worldgen.placedfeature.modifiers.PlacementModifier
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
) : Generator {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

fun DataPack.placedFeature(
	fileName: String = "placed_feature",
	feature: ConfiguredFeatureArgument,
	placementModifiers: List<PlacementModifier> = emptyList(),
): PlacedFeatureArgument {
	placedFeatures += PlacedFeature(fileName, feature, placementModifiers)
	return PlacedFeatureArgument(fileName, name)
}

fun DataPack.placedFeature(
	fileName: String = "placed_feature",
	feature: ConfiguredFeatureArgument,
	vararg placementModifiers: PlacementModifier,
): PlacedFeatureArgument {
	placedFeatures += PlacedFeature(fileName, feature, placementModifiers.toList())
	return PlacedFeatureArgument(fileName, name)
}

fun DataPack.placedFeature(
	fileName: String = "placed_feature",
	feature: ConfiguredFeatureArgument,
	placementModifiers: MutableList<PlacementModifier>.() -> Unit,
): PlacedFeatureArgument {
	placedFeatures += PlacedFeature(fileName, feature, buildList(placementModifiers))
	return PlacedFeatureArgument(fileName, name)
}
