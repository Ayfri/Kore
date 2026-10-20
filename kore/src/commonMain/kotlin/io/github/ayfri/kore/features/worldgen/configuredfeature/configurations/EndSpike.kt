package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import io.github.ayfri.kore.serializers.JsonSerialName
import io.github.ayfri.kore.serializers.TripleAsArray
import kotlinx.serialization.Serializable

/**
 * Configuration for the `end_spike` feature.
 *
 * Places the obsidian pillars of the End, each topped with an End crystal. Vanilla uses it in the End biome with no
 * [spikes], which generates the default random pillars.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#end_spike
 *
 * @property crystalBeamTarget The block position the beams of the End crystals point at, none when `null`.
 * @property crystalInvulnerable Whether the End crystals on the pillars are invulnerable, `false` when `null`.
 * @property spikes The pillars to place, the default random pillars when empty.
 */
@Serializable
data class EndSpike(
	var crystalBeamTarget: TripleAsArray<Int, Int, Int>? = null,
	var crystalInvulnerable: Boolean? = null,
	var spikes: List<EndSpikeEntry> = emptyList(),
) : FeatureConfig()

/**
 * A pillar of an [EndSpike].
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#end_spike
 *
 * @property centerX The X coordinate of the pillar's center, `0` when `null`.
 * @property centerZ The Z coordinate of the pillar's center, `0` when `null`.
 * @property guarded Whether an iron bars cage surrounds the crystal, `false` when `null`.
 * @property height The height of the pillar, `0` when `null`.
 * @property radius The radius of the pillar, `0` when `null`.
 */
@Serializable
data class EndSpikeEntry(
	@JsonSerialName("centerX")
	var centerX: Int? = null,
	@JsonSerialName("centerZ")
	var centerZ: Int? = null,
	var guarded: Boolean? = null,
	var height: Int? = null,
	var radius: Int? = null,
)

/**
 * Adds a pillar to [EndSpike.spikes], configured in [block].
 *
 * ```kotlin
 * endSpike("end_spike") {
 *     spike {
 *         centerX = 0
 *         centerZ = 42
 *         radius = 3
 *         height = 76
 *         guarded = true
 *     }
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#end_spike
 */
fun EndSpike.spike(block: EndSpikeEntry.() -> Unit) {
	spikes += EndSpikeEntry().apply(block)
}

/**
 * Creates an `end_spike` configured feature, its pillars being added with [spike] in [block].
 *
 * ```kotlin
 * endSpike("end_spike") {
 *     crystalInvulnerable = true
 *     spike {
 *         centerX = 0
 *         centerZ = 42
 *         radius = 3
 *         height = 76
 *         guarded = true
 *     }
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#end_spike
 */
fun ConfiguredFeatures.endSpike(fileName: String, block: EndSpike.() -> Unit = {}) =
	dp.configuredFeature(fileName, EndSpike().apply(block))
