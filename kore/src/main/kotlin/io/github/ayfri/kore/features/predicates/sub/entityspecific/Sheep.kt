package io.github.ayfri.kore.features.predicates.sub.entityspecific

import io.github.ayfri.kore.arguments.colors.FormattingColor
import io.github.ayfri.kore.features.predicates.sub.Entity
import kotlinx.serialization.Serializable

@Serializable
data class Sheep(val sheared: Boolean? = null, val color: FormattingColor? = null) : EntityTypeSpecific()

fun Entity.sheepTypeSpecific(sheared: Boolean? = null, color: FormattingColor? = null, block: Sheep.() -> Unit = {}) = apply {
	typeSpecific = Sheep(sheared, color).apply(block)
}
