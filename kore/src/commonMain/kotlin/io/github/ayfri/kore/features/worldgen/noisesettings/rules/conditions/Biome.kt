package io.github.ayfri.kore.features.worldgen.noisesettings.rules.conditions

import io.github.ayfri.kore.features.worldgen.noisesettings.rules.MaterialRulesScope
import io.github.ayfri.kore.generated.arguments.worldgen.types.BiomeArgument
import kotlinx.serialization.Serializable

/**
 * Represents a condition that checks whether the current position is in one of [biomeIs].
 *
 * @property biomeIs The biomes the condition matches.
 */
@Serializable
data class Biome(
	var biomeIs: List<BiomeArgument> = listOf(),
) : MaterialCondition()

/** Creates a [Biome] condition matching the biomes appended in [block]. */
fun MaterialRulesScope.biomes(block: MutableList<BiomeArgument>.() -> Unit) = Biome(buildList(block))

/** Creates a [Biome] condition matching [biome]. */
fun MaterialRulesScope.biomes(vararg biome: BiomeArgument) = Biome(biome.toList())
