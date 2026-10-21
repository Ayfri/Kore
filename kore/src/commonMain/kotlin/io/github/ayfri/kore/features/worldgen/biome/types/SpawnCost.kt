package io.github.ayfri.kore.features.worldgen.biome.types

import io.github.ayfri.kore.generated.arguments.types.EntityTypeArgument
import kotlinx.serialization.Serializable

@Serializable
data class SpawnCost(
	var energyBudget: Float,
	var charge: Float,
)

/** Caps the density of [type] with a [charge] spent from the shared [energyBudget], in a `spawnCosts { }` block. */
fun MutableMap<EntityTypeArgument, SpawnCost>.spawnCost(type: EntityTypeArgument, energyBudget: Float, charge: Float) {
	this[type] = SpawnCost(energyBudget, charge)
}
