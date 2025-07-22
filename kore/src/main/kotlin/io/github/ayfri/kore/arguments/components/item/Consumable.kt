package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.components.consumable.ConsumeEffect
import io.github.ayfri.kore.arguments.components.consumable.ConsumeEffects
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.SoundEventArgument
import io.github.ayfri.kore.serializers.LowercaseSerializer
import io.github.ayfri.kore.utils.snakeCase
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable(with = ConsumeAnimation.Companion.ConsumeAnimationSerializer::class)
enum class ConsumeAnimation {
	NONE,
	EAT,
	DRINK,
	BLOCK,
	BOW,
	SPEAR,
	CROSSBOW,
	SPYGLASS,
	TOOT_HORN,
	BRUSH;

	companion object {
		data object ConsumeAnimationSerializer : LowercaseSerializer<ConsumeAnimation>(entries)
	}
}

@Serializable
data class ConsumableComponent(
	@SerialName("consume_seconds")
	var consumeSeconds: Float,
	var animation: ConsumeAnimation,
	var sound: SoundEventArgument,
	@SerialName("has_consume_particles")
	var hasConsumeParticles: Boolean,
	@SerialName("on_consume_effects")
	var onConsumeEffects: Map<String, ConsumeEffect>? = null,
) : Component()

fun ComponentsScope.consumable(
	consumeSeconds: Float,
	animation: ConsumeAnimation,
	sound: SoundEventArgument,
	hasConsumeParticles: Boolean = true,
	block: ConsumableComponent.() -> Unit = {},
) = apply {
	this[ItemComponentTypes.CONSUMABLE] = ConsumableComponent(
		consumeSeconds,
		animation,
		sound,
		hasConsumeParticles
	).apply(block)
}

fun ConsumableComponent.onConsumeEffects(block: ConsumeEffects.() -> Unit) = apply {
	onConsumeEffects = ConsumeEffects().apply(block).effects.associateBy {
		"minecraft:${it.javaClass.simpleName.snakeCase()}"
	}
}
