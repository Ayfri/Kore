package commands

import arguments.int
import arguments.literal
import arguments.numbers.TimeNumber
import functions.Function

fun Function.weatherClear(duration: Int? = null) = addLine(command("weather", literal("clear"), int(duration)))
fun Function.weatherRain(duration: Int? = null) = addLine(command("weather", literal("rain"), int(duration)))
fun Function.weatherThunder(duration: Int? = null) = addLine(command("weather", literal("thunder"), int(duration)))

fun Function.weatherClear(duration: TimeNumber) = addLine(command("weather", literal("clear"), duration.asArg()))
fun Function.weatherRain(duration: TimeNumber) = addLine(command("weather", literal("rain"), duration.asArg()))
fun Function.weatherThunder(duration: TimeNumber) = addLine(command("weather", literal("thunder"), duration.asArg()))
