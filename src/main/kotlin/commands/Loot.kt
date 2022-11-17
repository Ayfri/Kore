package commands

import arguments.Argument
import arguments.SlotEntry
import arguments.int
import arguments.literal
import arguments.slot
import functions.Function
import kotlinx.serialization.Serializable
import serializers.LowercaseSerializer

class Target(private val fn: Function) {
	fun give(targets: Argument.Entity) = listOf(fn.literal("give"), targets)
	fun insert(pos: Argument.Coordinate) = listOf(fn.literal("insert"), pos)
	fun spawn(pos: Argument.Coordinate) = listOf(fn.literal("spawn"), pos)
	fun replaceBlock(pos: Argument.Coordinate, slot: SlotEntry, count: Int? = null) = listOf(fn.literal("replace"), pos, fn.slot(slot), fn.int(count))
	fun replaceEntity(entity: Argument.Entity, slot: SlotEntry, count: Int? = null) = listOf(fn.literal("replace"), entity, fn.slot(slot), fn.int(count))
}

@Serializable(Hand.Companion.HandSerializer::class)
enum class Hand {
	MAIN_HAND,
	OFF_HAND;
	
	companion object {
		val values = values()
		
		object HandSerializer : LowercaseSerializer<Hand>(values)
	}
}

class Source(private val fn: Function) {
	fun fish(lootTable: String, pos: Argument.Coordinate, tool: Argument.Item? = null) = listOf(fn.literal("fish"), fn.literal(lootTable), pos, tool)
	fun fish(lootTable: String, pos: Argument.Coordinate, hand: Hand) = listOf(fn.literal("fish"), fn.literal(lootTable), pos, fn.literal(hand.asArg()))
	fun loot(lootTable: String) = listOf(fn.literal("loot"), fn.literal(lootTable))
	fun kill(targets: Argument.Entity) = listOf(fn.literal("kill"), targets)
	fun mine(pos: Argument.Coordinate, tool: Argument.Item? = null) = listOf(fn.literal("mine"), pos, tool)
	fun mine(pos: Argument.Coordinate, hand: Hand) = listOf(fn.literal("mine"), pos, fn.literal(hand.asArg()))
}

class Loot(private val fn: Function) {
	lateinit var target: List<Argument?>
	lateinit var source: List<Argument?>
	
	fun target(block: Target.() -> List<Argument>) {
		target = Target(fn).block()
	}
	
	fun source(block: Source.() -> List<Argument>) {
		source = Source(fn).block()
	}
}

fun Function.loot(block: Loot.() -> Unit) = Loot(this).apply(block).let { loot ->
	addLine(command("loot", *loot.target.toTypedArray(), *loot.source.toTypedArray()))
}
