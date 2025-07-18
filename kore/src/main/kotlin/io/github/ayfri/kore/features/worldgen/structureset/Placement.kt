package io.github.ayfri.kore.features.worldgen.structureset

import io.github.ayfri.kore.generated.arguments.worldgen.types.BiomeArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.StructureSetArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlin.random.Random
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface Placement {
	var salt: Int
	var frequencyReductionMethod: FrequencyReductionMethod?
	var frequency: Double?
	var exclusionZone: ExclusionZone?
	var locateOffset: List<Int>?
}

fun Placement.exclusionZone(otherSet: StructureSetArgument, chunkCount: Int = 1) = apply {
	exclusionZone = ExclusionZone(otherSet, chunkCount)
}

@SerialName("minecraft:concentric_rings")
@Serializable
data class ConcentricRingsPlacement(
	override var salt: Int = Random.nextInt(Int.MAX_VALUE),
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

@SerialName("minecraft:random_spread")
@Serializable
data class RandomSpreadPlacement(
	override var salt: Int = Random.nextInt(Int.MAX_VALUE),
	override var frequencyReductionMethod: FrequencyReductionMethod? = null,
	override var frequency: Double? = null,
	override var exclusionZone: ExclusionZone? = null,
	override var locateOffset: List<Int>? = null,
	var spreadType: SpreadType? = null,
	var spacing: Int = 1,
	var separation: Int = 0,
) : Placement
