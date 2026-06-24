package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.colors.RGB
import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.MobEffectArgument
import io.github.ayfri.kore.generated.arguments.types.PotionArgument
import io.github.ayfri.kore.serializers.SinglePropertySimplifierSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** A single status effect entry used in potion/death-protection components. */
@Serializable
data class Effect(
	val id: MobEffectArgument,
	val duration: Int,
	val amplifier: Byte,
	var ambient: Boolean,
	@SerialName("show_particles")
	var showParticles: Boolean,
	@SerialName("show_icon")
	var showIcon: Boolean,
)

/**
 * Represents the `minecraft:potion_contents` item component, which configures potion type, color, and custom effects.
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#potion_contents
 */
@Serializable(with = PotionContentsComponent.Companion.PotionContentsComponentSerializer::class)
data class PotionContentsComponent(
	var potion: PotionArgument,
	@SerialName("custom_color")
	@Serializable(RGB.Companion.ColorAsDecimalSerializer::class)
	var customColor: RGB? = null,
	@SerialName("custom_effects")
	var customEffects: List<Effect>? = null,
	@SerialName("custom_name")
	var customName: String? = null,
) : Component() {
	companion object {
		data object PotionContentsComponentSerializer : SinglePropertySimplifierSerializer<PotionContentsComponent, PotionArgument>(
			PotionContentsComponent::class,
			PotionContentsComponent::potion,
		)
	}
}

/** Configures potion color, effects, and custom potion mixtures. */
fun ComponentsScope.potionContents(potion: PotionArgument, customColor: RGB? = null, customEffects: List<Effect>? = null) = apply {
	this[ItemComponentTypes.POTION_CONTENTS] = PotionContentsComponent(potion, customColor, customEffects)
}

fun ComponentsScope.potionContents(potion: PotionArgument, customColor: RGB? = null, vararg customEffects: Effect) = apply {
	this[ItemComponentTypes.POTION_CONTENTS] = PotionContentsComponent(potion, customColor, customEffects.toList())
}

fun ComponentsScope.potionContents(potion: PotionArgument, block: PotionContentsComponent.() -> Unit) = apply {
	this[ItemComponentTypes.POTION_CONTENTS] = PotionContentsComponent(potion, RGB(0, 0, 0), emptyList()).apply(block)
}

fun PotionContentsComponent.customEffect(
	id: MobEffectArgument,
	duration: Int,
	amplifier: Byte,
	ambient: Boolean,
	showParticles: Boolean,
	showIcon: Boolean,
) = apply {
	customEffects = (customEffects ?: listOf()) + Effect(id, duration, amplifier, ambient, showParticles, showIcon)
}

fun PotionContentsComponent.customEffects(vararg effects: Effect) {
	customEffects = effects.toList()
}
