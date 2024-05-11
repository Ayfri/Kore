package io.github.ayfri.kore.features.predicates.sub.entityspecific

import io.github.ayfri.kore.arguments.types.resources.VillagerTypeArgument
import io.github.ayfri.kore.features.predicates.sub.Entity
import kotlinx.serialization.Serializable

@Serializable
data class Villager(var variant: VillagerTypeArgument? = null) : EntityTypeSpecific()

fun Entity.villagerTypeSpecific(variant: VillagerTypeArgument? = null, block: Villager.() -> Unit = {}) = apply {
	typeSpecific = Villager(variant).apply(block)
}
