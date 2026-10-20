package io.github.ayfri.kore.arguments.components.matchers

import io.github.ayfri.kore.arguments.components.CollectionMatcher
import io.github.ayfri.kore.features.predicates.sub.MobEffectPredicate
import io.github.ayfri.kore.generated.arguments.PotionOrTagArgument
import io.github.ayfri.kore.generated.arguments.types.MobEffectArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Matches the `minecraft:potion_contents` component: the [potions] the item holds (a potion, a list of potions or a potion tag) and its
 * active [effects], each element being a map of mob effects to their [MobEffectPredicate].
 */
@Serializable
data class PotionContentsComponentMatcher(
	var effects: CollectionMatcher<Map<MobEffectArgument, MobEffectPredicate>>? = null,
	var potions: InlinableList<PotionOrTagArgument>? = null,
) : ComponentMatcher()

fun DataComponentPredicate.potionContents(block: PotionContentsComponentMatcher.() -> Unit) {
	matchers += PotionContentsComponentMatcher().apply(block)
}

fun DataComponentPredicate.potionContents(vararg potions: PotionOrTagArgument) = potionContents { potions(*potions) }

/**
 * Matches the effects of the potion contents.
 *
 * ```
 * effects { contains(mapOf(Effects.SPEED to mobEffectPredicate { amplifier = 1 })) }
 * ```
 */
fun PotionContentsComponentMatcher.effects(block: CollectionMatcher<Map<MobEffectArgument, MobEffectPredicate>>.() -> Unit) {
	effects = CollectionMatcher<Map<MobEffectArgument, MobEffectPredicate>>().apply(block)
}

fun PotionContentsComponentMatcher.potions(vararg potions: PotionOrTagArgument) {
	this.potions = potions.toList()
}
