package io.github.ayfri.kore.features.advancements.types.entityspecific

import io.github.ayfri.kore.features.advancements.serializers.IntRangeOrIntJson
import kotlinx.serialization.Serializable

@Serializable
data class Slime(var size: IntRangeOrIntJson? = null) : EntityTypeSpecific()
