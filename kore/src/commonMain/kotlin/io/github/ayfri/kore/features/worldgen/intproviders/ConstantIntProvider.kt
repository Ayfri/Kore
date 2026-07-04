package io.github.ayfri.kore.features.worldgen.intproviders

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Int provider that always returns [value].
 *
 * Serializes as a plain integer rather than a typed object.
 */
@Serializable
@SerialName("minecraft:constant")
data class ConstantIntProvider(val value: Int) : IntProvider
