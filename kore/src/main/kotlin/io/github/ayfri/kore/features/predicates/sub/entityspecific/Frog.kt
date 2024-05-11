package io.github.ayfri.kore.features.predicates.sub.entityspecific

import io.github.ayfri.kore.arguments.types.FrogVariantOrTagArgument
import io.github.ayfri.kore.features.predicates.sub.Entity
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class Frog(
	override var variant: InlinableList<FrogVariantOrTagArgument>,
) : EntityTypeSpecific(), VariantEntityTypeSpecific<FrogVariantOrTagArgument>

fun Entity.frogTypeSpecific(vararg variants: FrogVariantOrTagArgument, block: Frog.() -> Unit = {}) = apply {
	typeSpecific = Frog(variants.toList()).apply(block)
}
