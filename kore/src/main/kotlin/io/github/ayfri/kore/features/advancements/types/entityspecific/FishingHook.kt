package io.github.ayfri.kore.features.advancements.types.entityspecific

import kotlinx.serialization.Serializable

@Serializable
data class FishingHook(var inOpenWater: Boolean? = null) : EntityTypeSpecific()
