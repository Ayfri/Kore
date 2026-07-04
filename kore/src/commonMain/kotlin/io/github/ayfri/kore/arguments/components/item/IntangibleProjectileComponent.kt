package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:intangible_projectile` item component, which makes projectiles from this item pass through entities without collision.
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#intangible_projectile
 */
@Serializable
data object IntangibleProjectileComponent : Component()

/** Makes projectiles from this item pass through entities without collision. */
fun ComponentsScope.intangibleProjectile() = apply { this[ItemComponentTypes.INTANGIBLE_PROJECTILE] = IntangibleProjectileComponent }
