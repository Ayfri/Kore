package io.github.ayfri.kore.helpers.inventorymanager

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.generatedFunction

data class SlotEvent(
	val function: FunctionArgument,
	val type: SlotEventType,
)

context(DataPack)
fun SlotEventListener.duringTake(block: Function.() -> Unit) = event(SlotEventType.DURING_TAKEN, block)

context(Function)
fun SlotEventListener.duringTake(block: Function.() -> Unit) = with(datapack) { event(SlotEventType.DURING_TAKEN, block) }

fun SlotEventListener.onceTaken(function: FunctionArgument) = event(SlotEventType.ONCE_TAKEN, function)
context(DataPack)
fun SlotEventListener.onceTaken(block: Function.() -> Unit) = event(SlotEventType.ONCE_TAKEN, block)

context(Function)
fun SlotEventListener.onceTaken(block: Function.() -> Unit) = with(datapack) { event(SlotEventType.ONCE_TAKEN, block) }

fun SlotEventListener.onTake(function: FunctionArgument) = event(SlotEventType.WHEN_TAKEN, function)
context(DataPack)
fun SlotEventListener.onTake(block: Function.() -> Unit) = event(SlotEventType.WHEN_TAKEN, block)

context(Function)
fun SlotEventListener.onTake(block: Function.() -> Unit) = with(datapack) { event(SlotEventType.WHEN_TAKEN, block) }

fun SlotEventListener.event(type: SlotEventType, function: FunctionArgument) = events.add(SlotEvent(function, type))
context(DataPack)
fun SlotEventListener.event(type: SlotEventType, block: Function.() -> Unit) {
	val generatedFunction = generatedFunction("${type.name.lowercase()}_event_${hashCode()}", block = block)
	events.add(SlotEvent(generatedFunction, type))
}

fun SlotEventListener.duringTake(function: FunctionArgument) = event(SlotEventType.DURING_TAKEN, function)
