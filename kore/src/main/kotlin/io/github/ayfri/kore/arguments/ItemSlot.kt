package io.github.ayfri.kore.arguments

interface ItemSlot : Argument

interface ItemSlotWrapper : Argument

interface RangeItemSlot : Argument, ClosedRange<Int>, ItemSlotWrapper {
	fun all() = "${asString()}.*"
	operator fun get(index: Int) = ItemSlotType(start + index) { "${asString()}.$index" }
}

interface ItemSlotType : ItemSlot, ItemSlotWrapper {
	fun asIndex(): Int

	companion object {
		operator fun invoke(index: Int = 0, block: () -> String) = object : ItemSlotType {
			override fun asString() = block()
			override fun asIndex() = index
		}

		/**
		 * Get an [ItemSlotType] from an index.
		 * Some slots can overlap so the [fromEntity] and [fromPlayer] parameters are used to differentiate them.
		 */
		fun fromIndex(index: Int, fromEntity: Boolean = false, fromPlayer: Boolean = false) = when (index) {
			-106 -> WEAPON.OFFHAND
			in CONTAINER -> when {
				fromEntity && index in 0..8 -> HOTBAR[index]
				fromEntity && index in 9..35 -> INVENTORY[index - INVENTORY.start]
				else -> CONTAINER[index]
			}

			98 -> WEAPON
			99 -> WEAPON.OFFHAND
			100 -> ARMOR.FEET
			101 -> ARMOR.LEGS
			102 -> ARMOR.CHEST
			103 -> ARMOR.HEAD
			105 -> ARMOR.BODY
			in ENDERCHEST -> ENDERCHEST[index - ENDERCHEST.start]
			in VILLAGER -> VILLAGER[index - VILLAGER.start]
			400 -> HORSE.SADDLE
			499 -> when {
				fromPlayer -> PLAYER.CURSOR
				else -> HORSE.CHEST
			}

			in HORSE -> when {
				fromPlayer && index in PLAYER.CRAFTING -> PLAYER.CRAFTING[index - PLAYER.CRAFTING.start]
				else -> HORSE[index - HORSE.start]
			}

			else -> throw IllegalArgumentException("Invalid slot index: $index")
		}
	}
}

interface IndexedItemSlot : ItemSlot, RangeItemSlot {
	companion object {
		operator fun invoke(start: Int, endInclusive: Int, block: () -> String) = object : IndexedItemSlot {
			override fun asString() = block()

			override var start = start
			override var endInclusive = endInclusive
		}
	}
}

private fun ItemSlotWrapper.subType(name: String, index: Int) = ItemSlotType(index) { "${asString()}.$name" }

data object ARMOR : RangeItemSlot {
	override fun asString() = "armor"

	override val start = 100
	override val endInclusive = start + 5

	val FEET = subType("feet", 100)
	val LEGS = subType("legs", 101)
	val CHEST = subType("chest", 102)
	val HEAD = subType("head", 103)
	val BODY = subType("body", 105)
}

val CONTAINER = IndexedItemSlot(0, 53) { "container" }
val ENDERCHEST = IndexedItemSlot(200, 226) { "enderchest" }

data object HORSE : IndexedItemSlot {
	override var start = 500
	override val endInclusive = start + 14

	override fun asString() = "horse"

	val CHEST = subType("chest", 499)
	val SADDLE = subType("saddle", 400)
}

val HOTBAR = IndexedItemSlot(0, 8) { "hotbar" }
val INVENTORY = IndexedItemSlot(9, 35) { "inventory" }

data object PLAYER : ItemSlotWrapper {
	override fun asString() = "player"

	val CURSOR = subType("cursor", 499)
	val CRAFTING = IndexedItemSlot(500, 504) { "${asString()}.crafting" }
}

data object WEAPON : ItemSlotType, RangeItemSlot {
	override fun asIndex() = 98

	override fun asString() = "weapon"

	override val start = 98
	override val endInclusive = 9

	val MAINHAND = subType("mainhand", 98)
	val OFFHAND = subType("offhand", 99)
}

val VILLAGER = IndexedItemSlot(300, 307) { "villager" }
