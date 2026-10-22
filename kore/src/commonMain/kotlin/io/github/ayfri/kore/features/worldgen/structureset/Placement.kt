package io.github.ayfri.kore.features.worldgen.structureset

import io.github.ayfri.kore.generated.arguments.worldgen.types.BiomeArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.StructureSetArgument
import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.InlinableList
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@GeneratedSealedSerializer
@Serializable(with = Placement.Companion.PlacementSerializer::class)
sealed interface Placement {
	var salt: Int
	var frequencyReductionMethod: FrequencyReductionMethod?
	var frequency: Double?
	var exclusionZone: ExclusionZone?
	var locateOffset: List<Int>?

	companion object {
		data object PlacementSerializer :
			NamespacedPolymorphicSerializer<Placement>(placementSealedSerializer(), suffix = "Placement")
	}
}

fun Placement.exclusionZone(otherSet: StructureSetArgument, chunkCount: Int = 1) = apply {
	exclusionZone = ExclusionZone(otherSet, chunkCount)
}

@Serializable
data class ConcentricRingsPlacement(
	override var salt: Int = 0,
	override var frequencyReductionMethod: FrequencyReductionMethod? = null,
	override var frequency: Double? = null,
	override var exclusionZone: ExclusionZone? = null,
	override var locateOffset: List<Int>? = null,
	var distance: Int = 0,
	var spread: Int = 0,
	var count: Int = 1,
	var preferredBiomes: InlinableList<BiomeArgument> = emptyList(),
) : Placement

fun ConcentricRingsPlacement.preferredBiomes(vararg biomes: BiomeArgument) = apply {
	preferredBiomes += biomes
}

@Serializable
data class DimensionOriginPlacement(
	override var salt: Int = 0,
	override var frequencyReductionMethod: FrequencyReductionMethod? = null,
	override var frequency: Double? = null,
	override var exclusionZone: ExclusionZone? = null,
	override var locateOffset: List<Int>? = null,
) : Placement

@Serializable
data class RandomSpreadPlacement(
	override var salt: Int = 0,
	override var frequencyReductionMethod: FrequencyReductionMethod? = null,
	override var frequency: Double? = null,
	override var exclusionZone: ExclusionZone? = null,
	override var locateOffset: List<Int>? = null,
	var spreadType: SpreadType? = null,
	var spacing: Int = 1,
	var separation: Int = 0,
) : Placement
