package features.worldgen.configuredfeature.configurations

import kotlinx.serialization.Serializable

@Serializable
data class TwistingVines(
	var spreadWidth: Int = 0,
	var spreadHeight: Int = 0,
	var maxHeight: Int = 0,
) : FeatureConfig()

fun twistingVines(
	spreadWidth: Int = 0,
	spreadHeight: Int = 0,
	maxHeight: Int = 0,
	block: TwistingVines.() -> Unit = {},
) = TwistingVines(spreadWidth, spreadHeight, maxHeight).apply(block)
