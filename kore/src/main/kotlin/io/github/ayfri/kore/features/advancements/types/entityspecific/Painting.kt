package io.github.ayfri.kore.features.advancements.types.entityspecific

import io.github.ayfri.kore.arguments.types.resources.PaintingVariantArgument
import kotlinx.serialization.Serializable

@Serializable
data class Painting(var variant: PaintingVariantArgument? = null) : EntityTypeSpecific()
