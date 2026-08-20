package io.github.ayfri.kore.features.worldgen.blockpredicate

import io.github.ayfri.kore.generated.arguments.worldgen.BiomeOrTagArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Passes when the biome at the tested position is one of [biomes], given as a single biome, a list of biomes or a
 * biome tag.
 *
 * This predicate has no offset, it always tests the position being placed.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate#matching_biomes
 *
 * @property biomes The biomes to match, serialized as a bare string when there is exactly one.
 */
@Serializable
data class MatchingBiomes(
	var biomes: InlinableList<BiomeOrTagArgument> = emptyList(),
) : BlockPredicate()

/**
 * Creates a `matching_biomes` block predicate, passing when the biome at the tested position is one of [biomes].
 *
 * ```kotlin
 * blockPredicateFilter {
 *     predicate { matchingBiomes(Biomes.PLAINS, Biomes.SAVANNA) }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate#matching_biomes
 */
fun BlockPredicateScope.matchingBiomes(vararg biomes: BiomeOrTagArgument) =
	MatchingBiomes(biomes.toList()).also { addBlockPredicate(it) }

/**
 * Creates a `matching_biomes` block predicate, passing when the biome at the tested position is one of [biomes].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate#matching_biomes
 */
fun BlockPredicateScope.matchingBiomes(biomes: InlinableList<BiomeOrTagArgument>) =
	MatchingBiomes(biomes).also { addBlockPredicate(it) }
