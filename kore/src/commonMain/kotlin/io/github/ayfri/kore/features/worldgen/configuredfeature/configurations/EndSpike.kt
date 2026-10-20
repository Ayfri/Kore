package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
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
 * @property crystalInvulnerable Whether the End crystals on the pillars are invulnerable, `false` when `null`.
 * @property crystalBeamTarget The block position the beams of the End crystals point at.
 * @property spikes The pillars to place, the default random pillars when empty.
 */
@Serializable
data class EndSpike(
	var crystalInvulnerable: Boolean? = null,
	var crystalBeamTarget: TripleAsArray<Int, Int, Int> = TripleAsArray(0, 0, 0),
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
 * @property radius The radius of the pillar, `0` when `null`.
 * @property height The height of the pillar, `0` when `null`.
 * @property guarded Whether an iron bars cage surrounds the crystal, `false` when `null`.
 */
@Serializable
data class EndSpikeEntry(
	var centerX: Int? = null,
	var centerZ: Int? = null,
	var radius: Int? = null,
	var height: Int? = null,
	var guarded: Boolean? = null,
)

/**
 * Creates an `end_spike` configured feature, its pillars being added to [EndSpike.spikes] in [block].
 *
 * ```kotlin
 * endSpike("end_spike") {
 *     crystalInvulnerable = true
 *     spikes += EndSpikeEntry(centerX = 0, centerZ = 42, radius = 3, height = 76, guarded = true)
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
