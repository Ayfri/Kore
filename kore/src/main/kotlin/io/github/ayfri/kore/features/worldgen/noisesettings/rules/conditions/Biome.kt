package io.github.ayfri.kore.features.worldgen.noisesettings.rules.conditions

import io.github.ayfri.kore.arguments.types.resources.worldgen.BiomeArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class Biome(
	var biomeIs: InlinableList<BiomeArgument> = listOf(),
) : SurfaceRuleCondition()

fun biomes(block: MutableList<BiomeArgument>.() -> Unit) = Biome(buildList(block))
fun biomes(vararg biome: BiomeArgument): Biome = Biome(biome.toList())
