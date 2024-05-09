package io.github.ayfri.kore.arguments.components.types

import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ComponentTypes
import kotlinx.serialization.Serializable

@Serializable
data object FireResistantComponent : Component()

fun ComponentsScope.fireResistant() = apply { this[ComponentTypes.FIRE_RESISTANT] = FireResistantComponent }
