package io.github.ayfri.kore.helpers.inventorymanager

import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.functions.Function

data class SlotEvent(
	val function: FunctionArgument,
	val type: SlotEventType,
)

context(fn: Function)
fun SlotEventListener.duringTake(block: Function.() -> Unit) = with(fn.datapack) { duringTake(block) }

fun SlotEventListener.duringTake(function: FunctionArgument) = event(SlotEventType.DURING_TAKEN, function)

context(fn: Function)
fun SlotEventListener.onceTaken(block: Function.() -> Unit) = with(fn.datapack) { onceTaken(block) }

fun SlotEventListener.onceTaken(function: FunctionArgument) = event(SlotEventType.ONCE_TAKEN, function)

context(fn: Function)
fun SlotEventListener.onTake(block: Function.() -> Unit) = with(fn.datapack) { onTake(block) }

fun SlotEventListener.onTake(function: FunctionArgument) = event(SlotEventType.WHEN_TAKEN, function)

context(fn: Function)
fun SlotEventListener.event(type: SlotEventType, block: Function.() -> Unit) = with(fn.datapack) { event(type, block) }

fun SlotEventListener.event(type: SlotEventType, function: FunctionArgument) = events.add(SlotEvent(function, type))
