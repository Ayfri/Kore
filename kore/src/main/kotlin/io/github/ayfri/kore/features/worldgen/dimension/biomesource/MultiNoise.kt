package io.github.ayfri.kore.features.worldgen.dimension.biomesource

import io.github.ayfri.kore.features.worldgen.dimension.biomesource.multinoise.MultiNoiseEntry
import io.github.ayfri.kore.generated.BiomePresets
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.serializer

@Serializable(with = MultiNoise.Companion.MultiNoiseSerializer::class)
data class MultiNoise(
	var biomes: List<MultiNoiseEntry>? = null,
	var preset: BiomePresets? = null,
) : BiomeSource() {
	companion object {
		object MultiNoiseSerializer : KSerializer<MultiNoise> {
			private val multiNoiseBiomesDescriptor = buildClassSerialDescriptor("MultiNoiseBiomes") {
				element<List<MultiNoiseEntry>>("biomes")
			}

			private val multiNoisePresetDescriptor = buildClassSerialDescriptor("MultiNoisePreset") {
				element<BiomePresets>("preset")
			}

			override val descriptor = buildClassSerialDescriptor("MultiNoise")

			override fun deserialize(decoder: Decoder) = error("MultiNoise is not deserializable")

			override fun serialize(encoder: Encoder, value: MultiNoise) = encoder.encodeStructure(descriptor) {
				when {
					value.preset != null -> encodeSerializableElement(
						multiNoisePresetDescriptor,
						0,
						serializer<BiomePresets>(),
						value.preset!!
					)

					value.biomes != null -> encodeSerializableElement(
						multiNoiseBiomesDescriptor,
						0,
						serializer<List<MultiNoiseEntry>>(),
						value.biomes!!
					)

					else -> error("MultiNoise must have either a reference or an inline value")
				}
			}
		}
	}
}

fun multiNoise(biomes: List<MultiNoiseEntry>) = MultiNoise(biomes = biomes)
fun multiNoise(vararg biomes: MultiNoiseEntry) = MultiNoise(biomes = biomes.toList())
fun multiNoise(block: MutableList<MultiNoiseEntry>.() -> Unit) = MultiNoise(biomes = buildList(block))

fun multiNoise(preset: BiomePresets) = MultiNoise(preset = preset)
