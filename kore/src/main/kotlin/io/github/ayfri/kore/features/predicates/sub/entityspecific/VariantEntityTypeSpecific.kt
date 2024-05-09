package io.github.ayfri.kore.features.predicates.sub.entityspecific

import io.github.ayfri.kore.serializers.InlinableList

interface VariantEntityTypeSpecific<T> {
	var variant: InlinableList<T>
}

fun <T> VariantEntityTypeSpecific<T>.variants(vararg variants: T) {
	this.variant += variants.toList()
}
