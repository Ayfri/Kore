package io.github.ayfri.kore.features.predicates.sub.entityspecific

import io.github.ayfri.kore.arguments.types.resources.VillagerTypeArgument
import kotlinx.serialization.Serializable

@Serializable
data class Villager(var variant: VillagerTypeArgument? = null) : EntityTypeSpecific()
