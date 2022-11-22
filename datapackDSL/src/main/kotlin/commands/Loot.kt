package commands

import arguments.Argument
import arguments.Coordinate
import arguments.SlotEntry
import arguments.int
import arguments.literal
import arguments.slot
import functions.Function
import kotlinx.serialization.Serializable
import serializers.LowercaseSerializer

object Target {
	fun give(targets: Argument.Entity) = listOf(literal("give"), targets)
	fun insert(pos: Coordinate) = listOf(literal("insert"), pos)
	fun spawn(pos: Coordinate) = listOf(literal("spawn"), pos)
	fun replaceBlock(pos: Coordinate, slot: SlotEntry, count: Int? = null) = listOf(literal("replace"), pos, slot(slot), int(count))
	fun replaceEntity(entity: Argument.Entity, slot: SlotEntry, count: Int? = null) = listOf(literal("replace"), entity, slot(slot), int(count))
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

object Source {
	fun fish(lootTable: String, pos: Coordinate, tool: Argument.Item? = null) = listOf(literal("fish"), literal(lootTable), pos, tool)
	fun fish(lootTable: String, pos: Coordinate, hand: Hand) = listOf(literal("fish"), literal(lootTable), pos, literal(hand.asArg()))
	fun loot(lootTable: String) = listOf(literal("loot"), literal(lootTable))
	fun kill(targets: Argument.Entity) = listOf(literal("kill"), targets)
	fun mine(pos: Coordinate, tool: Argument.Item? = null) = listOf(literal("mine"), pos, tool)
	fun mine(pos: Coordinate, hand: Hand) = listOf(literal("mine"), pos, literal(hand.asArg()))
}

class Loot(private val fn: Function) {
	lateinit var target: List<Argument?>
	lateinit var source: List<Argument?>
	
	fun target(block: Target.() -> List<Argument>) {
		target = Target.block()
	}
	
	fun source(block: Source.() -> List<Argument>) {
		source = Source.block()
	}
}

fun Function.loot(block: Loot.() -> Command) = Loot(this).let { loot ->
	loot.block()
	addLine(command("loot", *loot.target.toTypedArray(), *loot.source.toTypedArray()))
}
