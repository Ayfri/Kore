package io.github.ayfri.kore.features.enchantments.effects.builders

import io.github.ayfri.kore.features.enchantments.effects.EnchantmentEffect
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.serializers.InlinableList
import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = PostAttackSpecifier.Companion.PostAttackSpecifierSerializer::class)
enum class PostAttackSpecifier {
	ATTACKER,
	DAMAGING_ENTITY,
	VICTIM;

	companion object {
		data object PostAttackSpecifierSerializer : LowercaseSerializer<PostAttackSpecifier>(entries)
	}
}

@Serializable
data class PostAttackConditionalEffect(
	var enchanted: PostAttackSpecifier,
	var affected: PostAttackSpecifier,
	var effect: EnchantmentEffect,
	var requirements: InlinableList<PredicateCondition>? = null,
)
