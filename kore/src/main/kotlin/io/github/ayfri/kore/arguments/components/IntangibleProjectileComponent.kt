package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.generated.ComponentTypes
import kotlinx.serialization.Serializable

@Serializable
data object IntangibleProjectileComponent : Component()

fun Components.intangibleProjectile() = apply { this[ComponentTypes.INTANGIBLE_PROJECTILE] = IntangibleProjectileComponent }
