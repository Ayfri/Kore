package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import kotlinx.serialization.Serializable

@Serializable
data class PointedDripstone(
	var chanceOfTallerDripstone: Double? = null,
	var chanceOfDirectionalSpread: Double? = null,
	var chanceOfSpreadRadius2: Double? = null,
	var chanceOfSpreadRadius3: Double? = null,
) : FeatureConfig()

fun pointedDripstone(
	chanceOfTallerDripstone: Double? = null,
	chanceOfDirectionalSpread: Double? = null,
	chanceOfSpreadRadius2: Double? = null,
	chanceOfSpreadRadius3: Double? = null,
	block: PointedDripstone.() -> Unit = {},
) = PointedDripstone(chanceOfTallerDripstone, chanceOfDirectionalSpread, chanceOfSpreadRadius2, chanceOfSpreadRadius3).apply(block)
