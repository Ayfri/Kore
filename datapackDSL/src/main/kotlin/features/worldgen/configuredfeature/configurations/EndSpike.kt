package features.worldgen.configuredfeature.configurations

import serializers.TripleAsArray
import kotlinx.serialization.Serializable

@Serializable
data class EndSpike(
	var crystalInvulnerable: Boolean? = null,
	var crystalBeamTarget: TripleAsArray<Int, Int, Int> = TripleAsArray(0, 0, 0),
	var spikes: List<Spike> = emptyList(),
) : FeatureConfig()

@Serializable
data class Spike(
	var centerX: Int? = null,
	var centerZ: Int? = null,
	var radius: Int? = null,
	var height: Int? = null,
	var guarded: Boolean? = null,
)
