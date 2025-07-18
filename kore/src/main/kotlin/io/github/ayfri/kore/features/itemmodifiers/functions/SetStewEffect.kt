package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.features.predicates.providers.NumberProvider
import io.github.ayfri.kore.features.predicates.providers.constant
import io.github.ayfri.kore.generated.arguments.types.PotionArgument
import kotlinx.serialization.Serializable

@Serializable
data class SetStewEffect(
	override var conditions: PredicateAsList? = null,
	val effects: List<PotionEffect>,
) : ItemFunction()

@Serializable
data class PotionEffect(
	var type: PotionArgument,
	var duration: NumberProvider = constant(0f),
)

fun ItemModifier.setStewEffect(effects: MutableList<PotionEffect>.() -> Unit) =
	SetStewEffect(effects = buildList(effects)).also { modifiers += it }

fun ItemModifier.setStewEffect(type: PotionArgument, duration: NumberProvider = constant(0f), block: SetStewEffect.() -> Unit = {}) {
	modifiers += SetStewEffect(
		effects = listOf(
			PotionEffect(
				type,
				duration
			)
		)
	).apply(block)
}

fun MutableList<PotionEffect>.potionEffect(type: PotionArgument, duration: NumberProvider = constant(0f)) {
	add(PotionEffect(type, duration))
}
