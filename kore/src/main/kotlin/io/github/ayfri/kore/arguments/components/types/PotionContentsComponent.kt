package io.github.ayfri.kore.arguments.components.types

import io.github.ayfri.kore.arguments.colors.RGB
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.types.resources.EffectArgument
import io.github.ayfri.kore.arguments.types.resources.PotionArgument
import io.github.ayfri.kore.generated.ComponentTypes
import io.github.ayfri.kore.serializers.SinglePropertySimplifierSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Effect(
	val id: EffectArgument,
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
) : Component() {
	companion object {
		data object PotionContentsComponentSerializer : SinglePropertySimplifierSerializer<PotionContentsComponent, PotionArgument>(
			PotionContentsComponent::class,
			PotionContentsComponent::potion,
		)
	}
}

fun ComponentsScope.potionContents(potion: PotionArgument, customColor: RGB? = null, customEffects: List<Effect>? = null) = apply {
	this[ComponentTypes.POTION_CONTENTS] = PotionContentsComponent(potion, customColor, customEffects)
}

fun ComponentsScope.potionContents(potion: PotionArgument, customColor: RGB? = null, vararg customEffects: Effect) = apply {
	this[ComponentTypes.POTION_CONTENTS] = PotionContentsComponent(potion, customColor, customEffects.toList())
}

fun ComponentsScope.potionContents(potion: PotionArgument, block: PotionContentsComponent.() -> Unit) = apply {
	this[ComponentTypes.POTION_CONTENTS] = PotionContentsComponent(potion, RGB(0, 0, 0), emptyList()).apply(block)
}

fun PotionContentsComponent.customEffect(
	id: EffectArgument,
	duration: Int,
	amplifier: Byte,
	ambient: Boolean,
	showParticles: Boolean,
	showIcon: Boolean,
) = apply {
	customEffects = (customEffects ?: listOf()) + Effect(id, duration, amplifier, ambient, showParticles, showIcon)
}
