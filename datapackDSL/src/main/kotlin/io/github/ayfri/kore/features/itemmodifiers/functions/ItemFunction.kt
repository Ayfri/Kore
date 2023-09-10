package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = ItemFunction.Companion.ItemFunctionSerializer::class)
sealed class ItemFunction {
	abstract var conditions: PredicateAsList?

	companion object {
		data object ItemFunctionSerializer : NamespacedPolymorphicSerializer<ItemFunction>(ItemFunction::class, outputName = "function")
	}
}

fun ItemFunction.conditions(block: Predicate.() -> Unit) {
	conditions = Predicate().apply(block)
}
