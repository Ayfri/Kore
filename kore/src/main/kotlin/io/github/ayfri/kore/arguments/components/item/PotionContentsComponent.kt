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
