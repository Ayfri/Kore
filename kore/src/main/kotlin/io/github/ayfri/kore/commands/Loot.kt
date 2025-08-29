package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.ItemSlotType
import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.int
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.features.loottables.LootTable
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.arguments.types.LootTableArgument
import io.github.ayfri.kore.serializers.LowercaseSerializer
import io.github.ayfri.kore.utils.asArg
import io.github.ayfri.kore.utils.snbtSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Encoder

data object LootTarget {
	fun give(targets: EntityArgument) = listOf(literal("give"), targets)
	fun insert(pos: Vec3) = listOf(literal("insert"), pos)
	fun spawn(pos: Vec3) = listOf(literal("spawn"), pos)
	fun replaceBlock(pos: Vec3, slot: ItemSlotType, count: Int? = null) = listOfNotNull(literal("replace"), literal("block"), pos, slot, int(count))
	fun replaceEntity(entity: EntityArgument, slot: ItemSlotType, count: Int? = null) =
		listOfNotNull(literal("replace"), literal("entity"), entity, slot, int(count))
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

data object LootSource {
	fun fish(lootTable: LootTableArgument, pos: Vec3, tool: ItemArgument? = null) = listOfNotNull(literal("fish"), lootTable, pos, tool)
	fun fish(pos: Vec3, tool: ItemArgument? = null, lootTable: LootTable.() -> Unit) = listOf(literal("fish"), literal(snbtSerializer.encodeToString(lootTable)), pos, tool)
	fun fish(lootTable: LootTableArgument, pos: Vec3, hand: Hand) = listOf(literal("fish"), lootTable, pos, literal(hand.asArg()))
	fun fish(pos: Vec3, hand: Hand, lootTable: LootTable.() -> Unit) = listOf(literal("fish"), literal(snbtSerializer.encodeToString(lootTable)), pos, literal(hand.asArg()))

	fun loot(lootTable: LootTableArgument) = listOf(literal("loot"), lootTable)
	fun loot(lootTable: LootTable.() -> Unit) = listOf(literal("loot"), literal(snbtSerializer.encodeToString(LootTable().apply(lootTable))))

	fun kill(targets: EntityArgument) = listOf(literal("kill"), targets)

	fun mine(pos: Vec3, tool: ItemArgument? = null) = listOfNotNull(literal("mine"), pos, tool)
	fun mine(pos: Vec3, hand: Hand) = listOf(literal("mine"), pos, literal(hand.asArg()))
}

class Loot {
	lateinit var target: List<Argument?>
	lateinit var source: List<Argument?>

	fun target(block: LootTarget.() -> List<Argument>) {
		target = LootTarget.block()
	}

	fun source(block: LootSource.() -> List<Argument>) {
		source = LootSource.block()
	}
}

fun Function.loot(block: Loot.() -> Unit) = Loot().let { loot ->
	loot.block()
	addLine(command("loot", *loot.target.toTypedArray(), *loot.source.toTypedArray()))
}

fun Function.loot(target: EntityArgument, source: LootSource.() -> List<Argument>) = loot {
	target { give(target) }
	source { source() }
}

fun Function.loot(target: EntityArgument, lootTable: LootTableArgument) = loot(target) { loot(lootTable) }
