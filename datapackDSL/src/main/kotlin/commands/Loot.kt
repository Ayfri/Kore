package commands

import arguments.*
import functions.Function
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Encoder
import serializers.LowercaseSerializer

object Target {
	fun give(targets: Argument.Entity) = listOf(literal("give"), targets)
	fun insert(pos: Vec3) = listOf(literal("insert"), pos)
	fun spawn(pos: Vec3) = listOf(literal("spawn"), pos)
	fun replaceBlock(pos: Vec3, slot: ItemSlotType, count: Int? = null) = listOf(literal("replace"), pos, slot, int(count))
	fun replaceEntity(entity: Argument.Entity, slot: ItemSlotType, count: Int? = null) =
		listOf(literal("replace"), entity, slot, int(count))
}

@Serializable(Hand.Companion.HandSerializer::class)
enum class Hand {
	MAIN_HAND,
	OFF_HAND;

	companion object {
		val values = values()

		object HandSerializer : LowercaseSerializer<Hand>(values) {
			override fun serialize(encoder: Encoder, value: Hand) {
				encoder.encodeString(value.name.lowercase().replace("_", ""))
			}
		}
	}
}

object Source {
	fun fish(lootTable: String, pos: Vec3, tool: Argument.Item? = null) = listOf(literal("fish"), literal(lootTable), pos, tool)
	fun fish(lootTable: String, pos: Vec3, hand: Hand) = listOf(literal("fish"), literal(lootTable), pos, literal(hand.asArg()))
	fun loot(lootTable: String) = listOf(literal("loot"), literal(lootTable))
	fun kill(targets: Argument.Entity) = listOf(literal("kill"), targets)
	fun mine(pos: Vec3, tool: Argument.Item? = null) = listOf(literal("mine"), pos, tool)
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
