package io.github.ayfri.kore.features.worldclock

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.generated.arguments.types.WorldClockArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Data-driven world clock definition.
 *
 * A world clock is a named ticker registered under `data/<namespace>/world_clock/<id>.json`.
 * Timelines and dimension types reference clocks to drive time-based behaviour.
 *
 * Produces `data/<namespace>/world_clock/<fileName>.json`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/World_clock
 */
@Serializable
data class WorldClock(
	@Transient
	override var fileName: String = "clock",
) : Generator("world_clock") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

/**
 * Creates and registers a world clock in this [DataPack].
 *
 * Produces `data/<namespace>/world_clock/<fileName>.json`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/World_clock
 */
fun DataPack.worldClock(
	fileName: String = "clock",
	init: WorldClock.() -> Unit = {},
): WorldClockArgument {
	val clock = WorldClock(fileName = fileName).apply(init)
	worldClocks += clock
	return WorldClockArgument(fileName, clock.namespace ?: name)
}
