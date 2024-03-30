package io.github.ayfri.kore.features.predicates.sub.entityspecific

import io.github.ayfri.kore.arguments.types.resources.FrogVariantArgument
import kotlinx.serialization.Serializable

@Serializable
data class Frog(var variant: FrogVariantArgument? = null) : EntityTypeSpecific()
