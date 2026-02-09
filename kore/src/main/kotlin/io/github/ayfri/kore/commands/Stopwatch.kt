package io.github.ayfri.kore.commands

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.types.literals.int
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.arguments.types.StopwatchArgument

/** Creates a stopwatch identifier with the given id and using the datapack namespace. */
context(dp: DataPack)
fun stopWatch(id: String) = StopwatchArgument(id, dp.name)

/** Creates a stopwatch identifier with the given id and using the function namespace. */
context(fn: Function)
fun stopWatch(id: String) = StopwatchArgument(id, fn.datapack.name)

/** Creates a stopwatch identifier with the given id and namespace, defaults to minecraft. */
fun stopWatch(id: String, namespace: String = "minecraft") = StopwatchArgument(id, namespace)

/** Registers a stopwatch with the given id. */
fun Function.stopwatchCreate(id: StopwatchArgument) = addLine(command("stopwatch", literal("create"), id))

/** Shows the elapsed time in seconds of the given stopwatch in chat. */
fun Function.stopwatchQuery(id: StopwatchArgument, scale: Int? = null) =
	addLine(command("stopwatch", literal("query"), id, int(scale)))

/** Restarts a stopwatch with the given id. */
fun Function.stopwatchRestart(id: StopwatchArgument) = addLine(command("stopwatch", literal("restart"), id))

/** Removes a stopwatch with the given id. */
fun Function.stopwatchRemove(id: StopwatchArgument) = addLine(command("stopwatch", literal("remove"), id))
