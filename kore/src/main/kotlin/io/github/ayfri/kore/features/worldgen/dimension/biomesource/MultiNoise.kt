package io.github.ayfri.kore.features.worldgen.dimension.biomesource

import io.github.ayfri.kore.features.worldgen.dimension.biomesource.multinoise.MultiNoiseEntry
import io.github.ayfri.kore.generated.BiomePresets
import io.github.ayfri.kore.serializers.EitherInlineSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KeepGeneratedSerializer
import kotlinx.serialization.Serializable

@OptIn(ExperimentalSerializationApi::class)
@KeepGeneratedSerializer
@Serializable(with = MultiNoise.Companion.MultiNoiseSerializer::class)
data class MultiNoise(
	var biomes: List<MultiNoiseEntry>? = null,
	var preset: BiomePresets? = null,
) : BiomeSource() {
	companion object {
		data object MultiNoiseSerializer :
			EitherInlineSerializer<MultiNoise>(generatedSerializer(), "preset", "biomes", inline = false)
	}
}

fun multiNoise(biomes: List<MultiNoiseEntry>) = MultiNoise(biomes = biomes)
fun multiNoise(vararg biomes: MultiNoiseEntry) = MultiNoise(biomes = biomes.toList())
fun multiNoise(block: MutableList<MultiNoiseEntry>.() -> Unit) = MultiNoise(biomes = buildList(block))

fun multiNoise(preset: BiomePresets) = MultiNoise(preset = preset)
