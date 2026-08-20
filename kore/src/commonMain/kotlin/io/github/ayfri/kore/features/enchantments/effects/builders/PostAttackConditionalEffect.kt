package io.github.ayfri.kore.features.enchantments.effects.builders

import io.github.ayfri.kore.features.enchantments.effects.EnchantmentEffect
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.serializers.InlinableList
import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

/**
 * Which side of a fight a `post_attack` entry looks at.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#post_attack
 */
@Serializable(with = PostAttackSpecifier.Companion.PostAttackSpecifierSerializer::class)
enum class PostAttackSpecifier {
	/** The entity that dealt the hit. */
	ATTACKER,

	/** The direct source of the damage, such as the arrow rather than the archer that shot it. */
	DAMAGING_ENTITY,

	/** The entity that took the hit. */
	VICTIM,
	;

	companion object {
		data object PostAttackSpecifierSerializer : LowercaseSerializer<PostAttackSpecifier>(entries)
	}
}

/**
 * One entry of the `post_attack` component, running [effect] on [affected] when [enchanted] carries the
 * enchantment.
 *
 * Built by `postAttack { on(enchanted, affected) { } }` rather than directly.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#post_attack
 *
 * @property enchanted The side that has to carry the enchantment for the entry to run.
 * @property affected The side the effect lands on.
 * @property effect The effect run by the entry.
 * @property requirements The conditions the entry runs under, always running when `null`.
 */
@Serializable
data class PostAttackConditionalEffect(
	var enchanted: PostAttackSpecifier,
	var affected: PostAttackSpecifier,
	var effect: EnchantmentEffect,
	var requirements: InlinableList<PredicateCondition>? = null,
)

/** Sets [PostAttackConditionalEffect.requirements] to the predicate conditions built in [block]. */
fun PostAttackConditionalEffect.requirements(block: Predicate.() -> Unit) =
	apply { requirements = Predicate().apply(block).predicateConditions }
