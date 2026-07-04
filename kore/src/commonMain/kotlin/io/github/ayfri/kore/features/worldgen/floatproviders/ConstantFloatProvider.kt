package io.github.ayfri.kore.features.worldgen.floatproviders

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Float provider that always returns [value].
 *
 * Serializes as a plain float rather than a typed object.
 */
@Serializable
@SerialName("minecraft:constant")
data class ConstantFloatProvider(val value: Float) : FloatProvider
