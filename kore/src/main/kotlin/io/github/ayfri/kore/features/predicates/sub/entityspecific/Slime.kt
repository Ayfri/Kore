package io.github.ayfri.kore.features.predicates.sub.entityspecific

import io.github.ayfri.kore.features.advancements.serializers.IntRangeOrIntJson
import io.github.ayfri.kore.features.predicates.sub.Entity
import kotlinx.serialization.Serializable

@Serializable
data class Slime(var size: IntRangeOrIntJson? = null) : EntityTypeSpecific()

fun Entity.slimeTypeSpecific(size: IntRangeOrIntJson? = null, block: Slime.() -> Unit = {}) = apply {
	typeSpecific = Slime(size).apply(block)
}
