package io.github.ayfri.kore.features.worldgen.blockpredicate

import io.github.ayfri.kore.generated.arguments.worldgen.BiomeOrTagArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Passes when the biome at the tested position is one of [biomes], given as a single biome, a list of biomes
 * or a biome tag.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate#matching_biomes
 */
@Serializable
data class MatchingBiomes(
	var biomes: InlinableList<BiomeOrTagArgument> = emptyList(),
) : BlockPredicate()

fun matchingBiomes(biomes: InlinableList<BiomeOrTagArgument> = emptyList()) = MatchingBiomes(biomes)

fun matchingBiomes(vararg biomes: BiomeOrTagArgument) = MatchingBiomes(biomes.toList())

fun MutableList<BlockPredicate>.matchingBiomes(biomes: InlinableList<BiomeOrTagArgument> = emptyList()) {
	this += MatchingBiomes(biomes)
}

fun MutableList<BlockPredicate>.matchingBiomes(vararg biomes: BiomeOrTagArgument) {
	this += MatchingBiomes(biomes.toList())
}
