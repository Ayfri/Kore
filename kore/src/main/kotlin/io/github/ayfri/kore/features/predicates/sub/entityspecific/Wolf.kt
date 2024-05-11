package io.github.ayfri.kore.features.predicates.sub.entityspecific

import io.github.ayfri.kore.arguments.types.WolfVariantOrTagArgument
import io.github.ayfri.kore.features.predicates.sub.Entity
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class Wolf(
	override var variant: InlinableList<WolfVariantOrTagArgument>,
) : EntityTypeSpecific(), VariantEntityTypeSpecific<WolfVariantOrTagArgument>

fun Entity.wolfTypeSpecific(vararg variants: WolfVariantOrTagArgument, block: Wolf.() -> Unit = {}) = apply {
	typeSpecific = Wolf(variants.toList()).apply(block)
}
