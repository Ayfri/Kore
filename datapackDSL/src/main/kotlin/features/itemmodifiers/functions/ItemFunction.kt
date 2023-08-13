package features.itemmodifiers.functions

import features.predicates.Predicate
import features.predicates.PredicateAsList
import serializers.NamespacedPolymorphicSerializer
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
