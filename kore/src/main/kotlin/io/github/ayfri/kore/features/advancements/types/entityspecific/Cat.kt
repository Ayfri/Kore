package io.github.ayfri.kore.features.advancements.types.entityspecific

import io.github.ayfri.kore.arguments.types.resources.CatVariantArgument
import kotlinx.serialization.Serializable

@Serializable
data class Cat(var variant: CatVariantArgument? = null) : EntityTypeSpecific()
