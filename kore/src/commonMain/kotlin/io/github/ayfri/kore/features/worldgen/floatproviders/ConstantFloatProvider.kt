package io.github.ayfri.kore.features.worldgen.floatproviders

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Always returns the same float.
 *
 * It is inlined when serialized, so it produces `1.5` rather than an object with a `type` field.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/float_provider
 *
 * @property value The value returned on every call.
 */
@Serializable
@SerialName("minecraft:constant")
data class ConstantFloatProvider(val value: Float) : FloatProvider
