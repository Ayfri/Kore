package io.github.ayfri.kore.features.worldgen.noisesettings.rules.conditions

import io.github.ayfri.kore.generated.arguments.worldgen.types.BiomeArgument
import kotlinx.serialization.Serializable

@Serializable
data class Biome(
	var biomeIs: List<BiomeArgument> = listOf(),
) : SurfaceRuleCondition()

fun biomes(block: MutableList<BiomeArgument>.() -> Unit) = Biome(buildList(block))
fun biomes(vararg biome: BiomeArgument): Biome = Biome(biome.toList())
