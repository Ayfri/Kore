package features.worldgen.noisesettings.rules.conditions

import arguments.Argument
import kotlinx.serialization.Serializable
import serializers.InlinableList

@Serializable
data class Biome(
	var biomeIs: InlinableList<Argument.Biome> = listOf(),
) : SurfaceRuleCondition()

fun biomes(block: MutableList<Argument.Biome>.() -> Unit) = Biome(buildList(block))
fun biomes(vararg biome: Argument.Biome): Biome = Biome(biome.toList())
