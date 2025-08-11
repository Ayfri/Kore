package io.github.ayfri.kore.arguments

/**
 * Represents a slot in an inventory or entity for item placement, is used in commands, NBT, and GUIs.
 * See: https://minecraft.wiki/w/Slot
 */
interface ItemSlot : Argument

/** Represents a named item slot, providing a string identifier. */
interface ItemSlotWrapper : Argument {
	override fun asString() = name()
	fun name(): String
}

/**
 * Represents a range of item slots, such as a group of slots in a container or inventory.
 * See: https://minecraft.wiki/w/Slot
 */
interface RangeItemSlot : Argument, ClosedRange<Int>, ItemSlotWrapper {
	/** Returns a string representing all slots in the range (e.g., "armor.*"). */
	fun all() = "${name()}.*"

	/**
	 * Returns the range as an ItemSlot.
	 * Alias for [asString] or the object itself for clarity.
	 *
	 * Example:
	 * ```
	 * val range = ARMOR.range()
	 * println(range) // "armor.*"
	 * ```
	 */
	fun range() = this

	/** Returns the [ItemSlotType] at the given index within the range. */
	operator fun get(index: Int) = ItemSlotType(start + index) { "${name()}.$index" }
}

/** Represents a specific item slot with an index. */
interface ItemSlotType : ItemSlot, ItemSlotWrapper {
	/** Returns the slot's index. */
	fun asIndex(): Int

	companion object {
		/** Creates an [ItemSlotType] with the given index and name provider. */
		operator fun invoke(index: Int = 0, block: () -> String) = object : ItemSlotType {
			override fun asIndex() = index
			override fun name() = block()
		}

		/**
		 * Returns an [ItemSlotType] for a given slot index.
		 * Some slots overlap; the [fromEntity], [fromPlayer], and [fromItemEntity] flags help disambiguate.
		 * See: https://minecraft.wiki/w/Slot
		 */
		fun fromIndex(
			index: Int,
			fromEntity: Boolean = false,
			fromPlayer: Boolean = false,
			fromItemEntity: Boolean = false,
		) = when (index) {
			-106 -> WEAPON.OFFHAND
			in CONTAINER -> when {
				fromItemEntity -> CONTENTS
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
			400 -> SADDLE
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

/** Represents a range of indexed item slots. */
interface IndexedItemSlot : ItemSlot, RangeItemSlot {
	override fun asString() = all()
	override fun range() = this

	companion object {
		/** Creates an [IndexedItemSlot] for the given range and name provider. */
		operator fun invoke(start: Int, endInclusive: Int, block: () -> String) = object : IndexedItemSlot {
			override fun name() = block()
			override var start = start
			override var endInclusive = endInclusive
		}
	}
}

/** Helper to create a named sub-slot for a given [ItemSlotWrapper]. */
private fun ItemSlotWrapper.subType(name: String, index: Int) = ItemSlotType(index) { "${asString()}.$name" }

/** Armor slots (feet, legs, chest, head, body). See: https://minecraft.wiki/w/Slot */
data object ARMOR : RangeItemSlot {
	override val start = 100
	override val endInclusive = start + 5

	override fun name() = "armor"

	/** The feet slot of the armor inventory. */
	val FEET = subType("feet", 100)

	/** The legs slot of the armor inventory. */
	val LEGS = subType("legs", 101)

	/** The chest slot of the armor inventory. */
	val CHEST = subType("chest", 102)

	/** The head slot of the armor inventory. */
	val HEAD = subType("head", 103)

	/** The body slot of the armor inventory. */
	val BODY = subType("body", 105)
}

/** General container slots (0–53). See: https://minecraft.wiki/w/Slot */
val CONTAINER = IndexedItemSlot(0, 53) { "container" }

/** Used for item entities. */
val CONTENTS = ItemSlotType(0) { "contents" }

/** Ender chest slots (200–226). */
val ENDERCHEST = IndexedItemSlot(200, 226) { "enderchest" }

/** Horse inventory slots (500–514). */
data object HORSE : IndexedItemSlot {
	override var start = 500
	override val endInclusive = start + 14

	override fun name() = "horse"

	/** The chest slot of the horse inventory. */
	val CHEST = subType("chest", 499)
}

/** Hotbar slots (0–8). */
val HOTBAR = IndexedItemSlot(0, 8) { "hotbar" }

/** Player inventory slots (9–35). */
val INVENTORY = IndexedItemSlot(9, 35) { "inventory" }

/** Player-specific slots. */
data object PLAYER : ItemSlotWrapper {
	override fun name() = "player"

	/** The cursor slot of the player inventory. */
	val CURSOR = subType("cursor", 499)

	/** The crafting slots of the player inventory. */
	val CRAFTING = IndexedItemSlot(500, 504) { "${asString()}.crafting" }
}

/** Saddle slot (400). */
val SADDLE = ItemSlotType(400) { "saddle" }

/** Weapon slots (mainhand: 98, offhand: 99). */
data object WEAPON : ItemSlotType, RangeItemSlot {
	override val start = 98
	override val endInclusive = 9

	override fun asIndex() = 98
	override fun name() = "weapon"

	/** The mainhand slot of the weapon inventory. */
	val MAINHAND = subType("mainhand", 98)

	/** The offhand slot of the weapon inventory. */
	val OFFHAND = subType("offhand", 99)
}

/** Villager inventory slots (300–307). */
val VILLAGER = IndexedItemSlot(300, 307) { "villager" }
