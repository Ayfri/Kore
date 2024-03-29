package io.github.ayfri.kore.features.worldgen.biome.types

import kotlinx.serialization.Serializable

@Serializable
data class SpawnCost(
	var energyBudget: Float,
	var charge: Float,
)
