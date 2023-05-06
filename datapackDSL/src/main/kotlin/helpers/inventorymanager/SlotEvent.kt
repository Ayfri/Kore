package helpers.inventorymanager

import DataPack
import arguments.Argument
import functions.Function
import functions.generatedFunction

data class SlotEvent(
	val function: Argument.Function,
	val type: SlotEventType,
)

context(DataPack)
fun SlotEventListener.duringTake(block: Function.() -> Unit) = event(SlotEventType.DURING_TAKEN, block)

context(Function)
fun SlotEventListener.duringTake(block: Function.() -> Unit) = with(datapack) { event(SlotEventType.DURING_TAKEN, block) }

fun SlotEventListener.onceTaken(function: Argument.Function) = event(SlotEventType.ONCE_TAKEN, function)
context(DataPack)
fun SlotEventListener.onceTaken(block: Function.() -> Unit) = event(SlotEventType.ONCE_TAKEN, block)

context(Function)
fun SlotEventListener.onceTaken(block: Function.() -> Unit) = with(datapack) { event(SlotEventType.ONCE_TAKEN, block) }

fun SlotEventListener.onTake(function: Argument.Function) = event(SlotEventType.WHEN_TAKEN, function)
context(DataPack)
fun SlotEventListener.onTake(block: Function.() -> Unit) = event(SlotEventType.WHEN_TAKEN, block)

context(Function)
fun SlotEventListener.onTake(block: Function.() -> Unit) = with(datapack) { event(SlotEventType.WHEN_TAKEN, block) }

fun SlotEventListener.event(type: SlotEventType, function: Argument.Function) = events.add(SlotEvent(function, type))
context(DataPack)
fun SlotEventListener.event(type: SlotEventType, block: Function.() -> Unit) {
	val generatedFunction = generatedFunction("${type.name.lowercase()}_event_${hashCode()}", block = block)
	events.add(SlotEvent(generatedFunction, type))
}

fun SlotEventListener.duringTake(function: Argument.Function) = event(SlotEventType.DURING_TAKEN, function)
