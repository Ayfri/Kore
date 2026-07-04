package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.numbers.TimeNumber
import io.github.ayfri.kore.arguments.types.literals.int
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.functions.Function

/**
 * Sets the weather to clear for an optional [duration].
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/weather)
 */
fun Function.weatherClear(duration: Int? = null) = addLine(command("weather", literal("clear"), int(duration)))

/**
 * Sets the weather to rain or snowfall for an optional [duration].
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/weather)
 */
fun Function.weatherRain(duration: Int? = null) = addLine(command("weather", literal("rain"), int(duration)))

/**
 * Sets the weather to a thunderstorm for an optional [duration].
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/weather)
 */
fun Function.weatherThunder(duration: Int? = null) = addLine(command("weather", literal("thunder"), int(duration)))

/**
 * Sets the weather to clear for an optional [duration].
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/weather)
 */
fun Function.weatherClear(duration: TimeNumber) = addLine(command("weather", literal("clear"), duration.asArg()))

/**
 * Sets the weather to rain or snowfall for an optional [duration].
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/weather)
 */
fun Function.weatherRain(duration: TimeNumber) = addLine(command("weather", literal("rain"), duration.asArg()))

/**
 * Sets the weather to a thunderstorm for an optional [duration].
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/weather)
 */
fun Function.weatherThunder(duration: TimeNumber) = addLine(command("weather", literal("thunder"), duration.asArg()))
