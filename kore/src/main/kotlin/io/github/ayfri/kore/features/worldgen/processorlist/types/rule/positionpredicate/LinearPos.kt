package io.github.ayfri.kore.features.worldgen.processorlist.types.rule.positionpredicate

import kotlinx.serialization.Serializable

@Serializable
data class LinearPos(
	var minDist: Int? = null,
	var maxDist: Int? = null,
	var minChance: Double? = null,
	var maxChance: Double? = null,
) : PositionPredicate()

fun linearPos(
	minDist: Int? = null,
	maxDist: Int? = null,
	minChance: Double? = null,
	maxChance: Double? = null,
	block: LinearPos.() -> Unit = {},
) = LinearPos(minDist, maxDist, minChance, maxChance).apply(block)
