package io.github.ayfri.kore.features.worldgen.noisesettings

import kotlinx.serialization.Serializable

/**
 * Represents the options for generating noise.
 *
 * @property minY The minimum Y value for the noise.
 * @property height The overall height of the noise.
 */
@Serializable
data class NoiseOptions(
	var minY: Int,
	var height: Int,
)
