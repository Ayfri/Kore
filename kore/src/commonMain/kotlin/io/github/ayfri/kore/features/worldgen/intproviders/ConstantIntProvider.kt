package io.github.ayfri.kore.features.worldgen.intproviders

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Always returns the same integer.
 *
 * It is inlined when serialized, so it produces `5` rather than an object with a `type` field.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Custom_world_generation/int_provider
 *
 * @property value The value returned on every call.
 */
@Serializable
@SerialName("minecraft:constant")
data class ConstantIntProvider(val value: Int) : IntProvider
