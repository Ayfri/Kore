package io.github.ayfri.kore.features.enchantments.effects.builders

import io.github.ayfri.kore.features.enchantments.effects.entity.EntityEffect
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/**
 * The list of conditional entity effects of the `post_attack` component.
 *
 * Each entry names which side of the fight has to carry the enchantment and which side the effect lands on, so
 * effects are added through `on(enchanted, affected) { }` rather than directly.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#post_attack
 */
@Serializable(with = PostAttackBuilder.Companion.PostAttackBuilderSerializer::class)
data class PostAttackBuilder(var effects: List<PostAttackConditionalEffect> = emptyList()) : EffectBuilder() {
	companion object {
		data object PostAttackBuilderSerializer :
			InlineAutoSerializer<PostAttackBuilder, List<PostAttackConditionalEffect>>(
				serializer<List<PostAttackConditionalEffect>>(),
				PostAttackBuilder::effects,
				::PostAttackBuilder,
				serialName = "PostAttackBuilder",
			)
	}
}

/** Collects the effects a `post_attack` entry runs, all sharing the same enchanted and affected sides. */
class PostAttackScope internal constructor(
	private val enchanted: PostAttackSpecifier,
	private val affected: PostAttackSpecifier,
	private val builder: PostAttackBuilder,
) : EntityEffectScope {
	override fun addEffect(effect: EntityEffect) {
		builder.effects += PostAttackConditionalEffect(enchanted, affected, effect, effect.requirements)
	}
}

/**
 * Appends every effect built in [block], each running when [enchanted] carries the enchantment and landing on
 * [affected].
 *
 * ```kotlin
 * postAttack {
 *     on(PostAttackSpecifier.ATTACKER, PostAttackSpecifier.VICTIM) {
 *         applyMobEffect(Effects.SLOWNESS) { duration(100) }
 *         damageEntity(DamageTypes.MAGIC, 1, 2) {
 *             requirements { weatherCheck(raining = true) }
 *         }
 *     }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#post_attack
 */
fun PostAttackBuilder.on(
	enchanted: PostAttackSpecifier,
	affected: PostAttackSpecifier,
	block: PostAttackScope.() -> Unit,
) {
	PostAttackScope(enchanted, affected, this).apply(block)
}
