package io.github.ayfri.kore.helpers.inventorymanager

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.generatedFunction

data class SlotEvent(
	val function: FunctionArgument,
	val type: SlotEventType,
)

context(dp: DataPack)
fun SlotEventListener.duringTake(block: Function.() -> Unit) = event(SlotEventType.DURING_TAKEN, block)

context(fn: Function)
fun SlotEventListener.duringTake(block: Function.() -> Unit) = with(fn.datapack) { event(SlotEventType.DURING_TAKEN, block) }

fun SlotEventListener.onceTaken(function: FunctionArgument) = event(SlotEventType.ONCE_TAKEN, function)
context(dp: DataPack)
fun SlotEventListener.onceTaken(block: Function.() -> Unit) = event(SlotEventType.ONCE_TAKEN, block)

context(fn: Function)
fun SlotEventListener.onceTaken(block: Function.() -> Unit) = with(fn.datapack) { event(SlotEventType.ONCE_TAKEN, block) }

fun SlotEventListener.onTake(function: FunctionArgument) = event(SlotEventType.WHEN_TAKEN, function)
context(dp: DataPack)
fun SlotEventListener.onTake(block: Function.() -> Unit) = event(SlotEventType.WHEN_TAKEN, block)

context(fn: Function)
fun SlotEventListener.onTake(block: Function.() -> Unit) = with(fn.datapack) { event(SlotEventType.WHEN_TAKEN, block) }

fun SlotEventListener.event(type: SlotEventType, function: FunctionArgument) = events.add(SlotEvent(function, type))

context(dp: DataPack)
fun SlotEventListener.event(type: SlotEventType, block: Function.() -> Unit) {
	val generatedFunction = dp.generatedFunction("${type.name.lowercase()}_event_${hashCode()}", block = block)
	events.add(SlotEvent(generatedFunction, type))
}

fun SlotEventListener.duringTake(function: FunctionArgument) = event(SlotEventType.DURING_TAKEN, function)
