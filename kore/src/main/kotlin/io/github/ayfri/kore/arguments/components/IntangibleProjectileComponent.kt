package io.github.ayfri.kore.arguments.components

import kotlinx.serialization.Serializable

@Serializable
data object IntangibleProjectileComponent : Component()

fun Components.intangibleProjectile() = apply { components["intangible_projectile"] = IntangibleProjectileComponent }
