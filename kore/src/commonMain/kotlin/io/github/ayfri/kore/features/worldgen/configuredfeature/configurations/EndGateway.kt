package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import io.github.ayfri.kore.serializers.TripleAsArray
import kotlinx.serialization.Serializable

/**
 * Configuration for the `end_gateway` feature.
 *
 * Places an End gateway, a gateway block framed by bedrock. Vanilla uses it with `exact` and an `exit` of `100 50 0`
 * in the end highlands.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#end_gateway
 *
 * @property exact Whether the gateway teleports entities to the exact [exit] position.
 * @property exit The block position the gateway leads to, none when `null`.
 */
@Serializable
data class EndGateway(
	var exact: Boolean = false,
	var exit: TripleAsArray<Int, Int, Int>? = null,
) : FeatureConfig()

/**
 * Creates an `end_gateway` configured feature leading to [exit], [exact]ly or not.
 *
 * ```kotlin
 * endGateway("end_gateway_return", exact = true, exit = Triple(100, 50, 0))
 * ```
 *
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#end_gateway
 */
fun ConfiguredFeatures.endGateway(
	fileName: String,
	exact: Boolean = false,
	exit: TripleAsArray<Int, Int, Int>? = null,
) = dp.configuredFeature(fileName, EndGateway(exact, exit))
