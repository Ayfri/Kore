package io.github.ayfri.kore.features.predicates.sub.entityspecific

import io.github.ayfri.kore.arguments.types.PaintingVariantOrTagArgument
import io.github.ayfri.kore.features.predicates.sub.Entity
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class Painting(
	override var variant: InlinableList<PaintingVariantOrTagArgument>,
) : EntityTypeSpecific(), VariantEntityTypeSpecific<PaintingVariantOrTagArgument>

fun Entity.paintingTypeSpecific(vararg variants: PaintingVariantOrTagArgument, block: Painting.() -> Unit = {}) = apply {
	typeSpecific = Painting(variants.toList()).apply(block)
}
