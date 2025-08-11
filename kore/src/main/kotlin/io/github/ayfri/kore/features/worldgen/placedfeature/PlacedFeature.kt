package io.github.ayfri.kore.features.worldgen.placedfeature

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.features.worldgen.placedfeature.modifiers.PlacementModifier
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.PlacedFeatureArgument
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Data-driven placed feature.
 *
 * Wraps a configured feature with a set of placement modifiers (count, rarity, height range,
 * biome filter, etc.). It describes WHERE and HOW OFTEN to place the configured feature.
 *
 * JSON format reference: https://minecraft.wiki/w/Placed_feature
 */
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

/**
 * Creates a placed feature that references an existing configured feature.
 *
 * Use the builder to add placement modifiers such as counts, height ranges, and biome filters.
 *
 * Produces `data/<namespace>/worldgen/placed_feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/worldgen
 */
fun DataPack.placedFeature(
	fileName: String = "placed_feature",
	feature: ConfiguredFeatureArgument,
	block: PlacedFeature.() -> Unit,
): PlacedFeatureArgument {
	val placedFeature = PlacedFeature(fileName, feature).apply(block)
	placedFeatures += placedFeature
	return PlacedFeatureArgument(fileName, placedFeature.namespace ?: name)
}
