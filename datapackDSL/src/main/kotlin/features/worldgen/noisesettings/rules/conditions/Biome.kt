package features.worldgen.noisesettings.rules.conditions

import arguments.types.resources.BiomeArgument
import kotlinx.serialization.Serializable
import serializers.InlinableList

@Serializable
data class Biome(
	var biomeIs: InlinableList<BiomeArgument> = listOf(),
) : SurfaceRuleCondition()

fun biomes(block: MutableList<BiomeArgument>.() -> Unit) = Biome(buildList(block))
fun biomes(vararg biome: BiomeArgument): Biome = Biome(biome.toList())
