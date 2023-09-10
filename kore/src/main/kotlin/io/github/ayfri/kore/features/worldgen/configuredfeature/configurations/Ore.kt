package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.Target
import kotlinx.serialization.Serializable

@Serializable
data class Ore(
	var size: Int = 0,
	var discardChanceOnAirExposure: Double = 0.0,
	var targets: List<Target> = emptyList(),
) : FeatureConfig()

fun ore(
	size: Int = 0,
	discardChanceOnAirExposure: Double = 0.0,
	targets: List<Target> = emptyList(),
	block: Ore.() -> Unit = {},
) = Ore(size, discardChanceOnAirExposure, targets).apply(block)

fun ore(
	size: Int = 0,
	discardChanceOnAirExposure: Double = 0.0,
	vararg targets: Target,
) = Ore(size, discardChanceOnAirExposure, targets.toList())

fun Ore.targets(list: List<Target>) = run { targets = list }
fun Ore.targets(vararg targets: Target) = run { this.targets = targets.toList() }
fun Ore.targets(block: MutableList<Target>.() -> Unit) = run { targets = buildList(block) }
