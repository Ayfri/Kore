package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.constant
import kotlinx.serialization.Serializable

@Serializable
data class BasaltColumns(
	var reach: IntProvider = constant(0),
	var height: IntProvider = constant(0),
) : FeatureConfig()

fun basaltColumns(reach: IntProvider = constant(0), height: IntProvider = constant(0)) = BasaltColumns(reach, height)
fun basaltColumns(reach: Int, height: Int) = BasaltColumns(constant(reach), constant(height))
