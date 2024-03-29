package io.github.ayfri.kore.features.advancements.types.entityspecific

import io.github.ayfri.kore.features.advancements.serializers.IntRangeOrIntJson
import io.github.ayfri.kore.features.advancements.types.Entity
import kotlinx.serialization.Serializable

@Serializable
data class Lightning(
	var blocksSetOnFire: IntRangeOrIntJson? = null,
	var entityStruck: Entity? = null,
) : EntityTypeSpecific()
