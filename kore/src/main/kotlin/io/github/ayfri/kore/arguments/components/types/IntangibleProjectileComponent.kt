package io.github.ayfri.kore.arguments.components.types

import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ComponentTypes
import kotlinx.serialization.Serializable

@Serializable
data object IntangibleProjectileComponent : Component()

fun ComponentsScope.intangibleProjectile() = apply { this[ComponentTypes.INTANGIBLE_PROJECTILE] = IntangibleProjectileComponent }
