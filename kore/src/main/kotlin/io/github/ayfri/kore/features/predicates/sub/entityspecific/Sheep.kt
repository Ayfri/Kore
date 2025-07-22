package io.github.ayfri.kore.features.predicates.sub.entityspecific

import io.github.ayfri.kore.features.predicates.sub.Entity
import kotlinx.serialization.Serializable

@Serializable
data class Sheep(val sheared: Boolean? = null) : EntityTypeSpecific()

fun Entity.sheepTypeSpecific(sheared: Boolean? = null, block: Sheep.() -> Unit = {}) = apply {
	typeSpecific = Sheep(sheared).apply(block)
}
