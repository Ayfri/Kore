package commands

import arguments.*
import functions.Function
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Encoder
import serializers.LowercaseSerializer
import utils.asArg

object Target {
	fun give(targets: Argument.Entity) = listOf(literal("give"), targets)
	fun insert(pos: Vec3) = listOf(literal("insert"), pos)
	fun spawn(pos: Vec3) = listOf(literal("spawn"), pos)
	fun replaceBlock(pos: Vec3, slot: ItemSlotType, count: Int? = null) = listOfNotNull(literal("replace"), pos, slot, int(count))
	fun replaceEntity(entity: Argument.Entity, slot: ItemSlotType, count: Int? = null) =
		listOfNotNull(literal("replace"), entity, slot, int(count))
}

@Serializable(Hand.Companion.HandSerializer::class)
enum class Hand {
	MAIN_HAND,
	OFF_HAND;

	companion object {
		data object HandSerializer : LowercaseSerializer<Hand>(entries) {
			override fun serialize(encoder: Encoder, value: Hand) {
				encoder.encodeString(value.name.lowercase().replace("_", ""))
			}
		}
	}
}

object Source {
	fun fish(lootTable: Argument.LootTable, pos: Vec3, tool: Argument.Item? = null) = listOfNotNull(literal("fish"), lootTable, pos, tool)
	fun fish(lootTable: Argument.LootTable, pos: Vec3, hand: Hand) = listOf(literal("fish"), lootTable, pos, literal(hand.asArg()))
	fun loot(lootTable: Argument.LootTable) = listOf(literal("loot"), lootTable)
	fun kill(targets: Argument.Entity) = listOf(literal("kill"), targets)
	fun mine(pos: Vec3, tool: Argument.Item? = null) = listOfNotNull(literal("mine"), pos, tool)
	fun mine(pos: Vec3, hand: Hand) = listOf(literal("mine"), pos, literal(hand.asArg()))
}

class Loot {
	lateinit var target: List<Argument?>
	lateinit var source: List<Argument?>

	fun target(block: Target.() -> List<Argument>) {
		target = Target.block()
	}

	fun source(block: Source.() -> List<Argument>) {
		source = Source.block()
	}
}

fun Function.loot(block: Loot.() -> Unit) = Loot().let { loot ->
	loot.block()
	addLine(command("loot", *loot.target.toTypedArray(), *loot.source.toTypedArray()))
}

fun Function.loot(target: Argument.Entity, source: Source.() -> List<Argument>) = loot {
	target { give(target) }
	source { source() }
}

fun Function.loot(target: Argument.Entity, lootTable: Argument.LootTable) = loot(target) { loot(lootTable) }
