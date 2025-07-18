package io.github.ayfri.kore.features.predicates.sub.entityspecific

import io.github.ayfri.kore.features.predicates.sub.Entity
import io.github.ayfri.kore.generated.arguments.PigVariantOrTagArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class Pig(
	override var variant: InlinableList<PigVariantOrTagArgument>,
) : EntityTypeSpecific(), VariantEntityTypeSpecific<PigVariantOrTagArgument>

fun Entity.pigTypeSpecific(vararg variants: PigVariantOrTagArgument, block: Pig.() -> Unit = {}) = apply {
	typeSpecific = Pig(variants.toList()).apply(block)
}
