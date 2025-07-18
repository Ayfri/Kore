package io.github.ayfri.kore.features.predicates.sub.entityspecific

import io.github.ayfri.kore.features.predicates.sub.Entity
import io.github.ayfri.kore.generated.arguments.CatVariantOrTagArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class Cat(
	override var variant: InlinableList<CatVariantOrTagArgument>,
) : EntityTypeSpecific(), VariantEntityTypeSpecific<CatVariantOrTagArgument>

fun Entity.catTypeSpecific(vararg variants: CatVariantOrTagArgument, block: Cat.() -> Unit = {}) = apply {
	typeSpecific = Cat(variants.toList()).apply(block)
}
