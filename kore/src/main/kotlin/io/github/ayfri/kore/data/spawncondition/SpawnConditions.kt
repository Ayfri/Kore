package io.github.ayfri.kore.data.spawncondition

import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrDouble
import io.github.ayfri.kore.data.spawncondition.types.Biome
import io.github.ayfri.kore.data.spawncondition.types.MoonBrightness
import io.github.ayfri.kore.data.spawncondition.types.Structure
import io.github.ayfri.kore.generated.arguments.worldgen.BiomeOrTagArgument
import io.github.ayfri.kore.generated.arguments.worldgen.ConfiguredStructureOrTagArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

/** A list of [VariantSpawnEntry]s, this is a builder helper. */
@Serializable(with = SpawnConditions.Companion.SpawnConditionsSerializer::class)
data class SpawnConditions(var entries: List<VariantSpawnEntry> = mutableListOf()) {
	companion object {
		data object SpawnConditionsSerializer : InlineAutoSerializer<SpawnConditions>(SpawnConditions::class)
	}
}

/** Adds a variant spawn entry without any condition. */
fun SpawnConditions.add(priority: Int) = apply {
	entries += VariantSpawnEntry(priority)
}

/** Adds a [Biome] variant spawn entry to the list, meaning entities will spawn in the given biomes. */
fun SpawnConditions.biome(priority: Int, biomes: List<BiomeOrTagArgument>) = apply {
	entries += VariantSpawnEntry(priority, Biome(biomes))
}

/** Adds a [Biome] variant spawn entry to the list, meaning entities will spawn in the given biomes. */
fun SpawnConditions.biome(priority: Int, vararg biomes: BiomeOrTagArgument) = apply {
	entries += VariantSpawnEntry(priority, Biome(biomes.toList()))
}

/** Adds a [MoonBrightness] variant spawn entry to the list, meaning entities will spawn in the given biomes. */
fun SpawnConditions.moonBrightness(priority: Int, minBrightness: Double, maxBrightness: Double) = apply {
	entries += VariantSpawnEntry(priority, MoonBrightness(rangeOrDouble(minBrightness, maxBrightness)))
}

/** Adds a [MoonBrightness] variant spawn entry to the list, meaning entities will spawn in the given biomes. */
fun SpawnConditions.moonBrightness(priority: Int, brightness: Double) = apply {
	entries += VariantSpawnEntry(priority, MoonBrightness(rangeOrDouble(brightness)))
}

/** Adds a [Structure] variant spawn entry to the list, meaning entities will spawn in structures. */
fun SpawnConditions.structures(priority: Int, structures: List<ConfiguredStructureOrTagArgument>) = apply {
	entries += VariantSpawnEntry(priority, Structure(structures))
}

/** Adds a [Structure] variant spawn entry to the list, meaning entities will spawn in structures. */
fun SpawnConditions.structures(priority: Int, vararg structures: ConfiguredStructureOrTagArgument) = apply {
	entries += VariantSpawnEntry(priority, Structure(structures.toList()))
}
