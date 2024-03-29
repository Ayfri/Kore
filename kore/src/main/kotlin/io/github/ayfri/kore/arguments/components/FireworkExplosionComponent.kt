package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.colors.RGB
import io.github.ayfri.kore.generated.ComponentTypes
import io.github.ayfri.kore.serializers.ToStringSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable(with = FireworkExplosionShape.Companion.FireworkExplosionShapeSerializer::class)
enum class FireworkExplosionShape {
	SMALL_BALL,
	LARGE_BALL,
	STAR,
	CREEPER,
	BURST;

	override fun toString() = name.lowercase()

	companion object {
		object FireworkExplosionShapeSerializer : ToStringSerializer<FireworkExplosionShape>()
	}
}

@Serializable
data class FireworkExplosionComponent(
	var shape: FireworkExplosionShape,
	var colors: List<@Serializable(RGB.Companion.ColorAsDecimalSerializer::class) RGB>? = null,
	@SerialName("fade_colors")
	var fadeColors: List<@Serializable(RGB.Companion.ColorAsDecimalSerializer::class) RGB>? = null,
	@SerialName("has_trail")
	var hasTrail: Boolean? = null,
	@SerialName("has_flicker")
	var hasFlicker: Boolean? = null,
) : Component()

fun Components.fireworkExplosion(
	shape: FireworkExplosionShape,
	colors: List<Color>? = null,
	fadeColors: List<Color>? = null,
	hasTrail: Boolean? = null,
	hasFlicker: Boolean? = null,
) = apply {
	components["firework_explosion"] =
		FireworkExplosionComponent(shape, colors?.map(Color::toRGB), fadeColors?.map(Color::toRGB), hasTrail, hasFlicker)
}

fun Components.fireworkExplosion(shape: FireworkExplosionShape, block: FireworkExplosionComponent.() -> Unit) = apply {
	this[ComponentTypes.FIREWORK_EXPLOSION] = FireworkExplosionComponent(shape).apply(block)
}

fun FireworkExplosionComponent.colors(vararg colors: Color) = apply {
	this.colors = (this.colors ?: mutableListOf()) + colors.map(Color::toRGB)
}

fun FireworkExplosionComponent.fadeColors(vararg colors: Color) = apply {
	this.fadeColors = (this.fadeColors ?: mutableListOf()) + colors.map(Color::toRGB)
}
