package io.github.ayfri.kore.features.worldgen.densityfunction.types

import kotlinx.serialization.Serializable

@Serializable
data class YClampedGradient(
	var fromY: Int = 0,
	var toY: Int = 0,
	var fromValue: Double = 0.0,
	var toValue: Double = 0.0,
) : DensityFunctionType()

fun yClampedGradient(
	fromY: Int = 0,
	toY: Int = 0,
	fromValue: Double = 0.0,
	toValue: Double = 0.0,
) = YClampedGradient(fromY, toY, fromValue, toValue)

fun yClampedGradient(block: YClampedGradient.() -> Unit = {}) = YClampedGradient().apply(block)
