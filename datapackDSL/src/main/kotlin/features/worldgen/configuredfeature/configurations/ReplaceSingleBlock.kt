package features.worldgen.configuredfeature.configurations

import features.worldgen.configuredfeature.Target
import kotlinx.serialization.Serializable

@Serializable
data class ReplaceSingleBlock(
	var targets: List<Target> = emptyList(),
) : FeatureConfig()

fun replaceSingleBlock(targets: List<Target> = emptyList()) = ReplaceSingleBlock(targets)

fun replaceSingleBlock(vararg targets: Target) = ReplaceSingleBlock(targets.toList())
