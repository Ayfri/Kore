package io.github.ayfri.kore.arguments.components.types

import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ComponentTypes
import kotlinx.serialization.Serializable

@Serializable
data object GliderComponent : Component()

fun ComponentsScope.glider() = apply {
	this[ComponentTypes.GLIDER] = GliderComponent
} 