package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = SwingAnimationType.Companion.SwingAnimationTypeSerializer::class)
enum class SwingAnimationType {
	NONE,
	STAB,
	WHACK;

	companion object {
		data object SwingAnimationTypeSerializer : LowercaseSerializer<SwingAnimationType>(entries)
	}
}

@Serializable
data class SwingAnimationComponent(
	var duration: Int? = null,
	var type: SwingAnimationType? = null,
) : Component()

fun ComponentsScope.swingAnimation(block: SwingAnimationComponent.() -> Unit = {}) = apply {
	this[ItemComponentTypes.SWING_ANIMATION] = SwingAnimationComponent().apply(block)
}

fun ComponentsScope.swingAnimation(type: SwingAnimationType? = null, duration: Int? = null) = apply {
	this[ItemComponentTypes.SWING_ANIMATION] = SwingAnimationComponent(duration, type)
}
