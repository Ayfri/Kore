package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.arguments.colors.RGB
import io.github.ayfri.kore.arguments.types.resources.EffectArgument
import io.github.ayfri.kore.arguments.types.resources.PotionArgument
import kotlinx.serialization.Serializable

@Serializable
data class Effect(
	val id: EffectArgument,
	val duration: Int,
	val amplifier: Byte,
	var ambient: Boolean,
	var showParticles: Boolean,
	var showIcon: Boolean,
)

@Serializable
data class PotionContentsComponent(
	var potion: PotionArgument,
	@Serializable(RGB.Companion.ColorAsDecimalSerializer::class) var customColor: RGB,
	var customEffects: List<Effect>,
) : Component()

fun Components.potionContents(potion: PotionArgument, customColor: RGB, customEffects: List<Effect>) = apply {
	components["potion_contents"] = PotionContentsComponent(potion, customColor, customEffects)
}

fun Components.potionContents(potion: PotionArgument, customColor: RGB, vararg customEffects: Effect) = apply {
	components["potion_contents"] = PotionContentsComponent(potion, customColor, customEffects.toList())
}

fun Components.potionContents(potion: PotionArgument, block: PotionContentsComponent.() -> Unit) = apply {
	components["potion_contents"] = PotionContentsComponent(potion, RGB(0, 0, 0), emptyList()).apply(block)
}

fun PotionContentsComponent.customEffect(
	id: EffectArgument,
	duration: Int,
	amplifier: Byte,
	ambient: Boolean,
	showParticles: Boolean,
	showIcon: Boolean,
) = apply {
	customEffects += Effect(id, duration, amplifier, ambient, showParticles, showIcon)
}
