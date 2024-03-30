package io.github.ayfri.kore.features.predicates.sub.entityspecific

import kotlinx.serialization.Serializable

@Serializable
data class FishingHook(var inOpenWater: Boolean? = null) : EntityTypeSpecific()
