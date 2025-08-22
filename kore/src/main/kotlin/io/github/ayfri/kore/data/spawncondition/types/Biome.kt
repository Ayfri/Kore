package io.github.ayfri.kore.data.spawncondition.types

import io.github.ayfri.kore.generated.arguments.worldgen.BiomeOrTagArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class Biome(var biomes: InlinableList<BiomeOrTagArgument>) : VariantCondition
