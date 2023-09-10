package io.github.ayfri.kore.features.worldgen.densityfunction.types

import kotlinx.serialization.Serializable

@Serializable
data class OldBlendedNoise(
	var xzScale: Double = 0.0,
	var yScale: Double = 0.0,
	var xzFactor: Double = 0.0,
	var yFactor: Double = 0.0,
	var smearScaleMultiplier: Double = 0.0,
) : DensityFunctionType()

fun oldBlendedNoise(
	xzScale: Double = 0.0,
	yScale: Double = 0.0,
	xzFactor: Double = 0.0,
	yFactor: Double = 0.0,
	smearScaleMultiplier: Double = 0.0,
) = OldBlendedNoise(xzScale, yScale, xzFactor, yFactor, smearScaleMultiplier)

fun oldBlendedNoise(block: OldBlendedNoise.() -> Unit = {}) = OldBlendedNoise().apply(block)
