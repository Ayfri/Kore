package io.github.ayfri.kore.arguments

/**
 * A slot selection written as a slot string in commands and slot sources: one slot (`armor.chest`, an [ItemSlotType])
 * or every slot of a range (`hotbar.*`, a [RangeItemSlot]).
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Slot
 */
interface ItemSlot : SlotsArgument

/** A named node of the slot tree, like `armor` or `player`, which isn't a slot by itself unless it is an [ItemSlot] too. */
interface ItemSlotWrapper : Argument {
	override fun asString() = name()
	fun name(): String
}

/**
 * A range of slots, like the hotbar or the armor, written `<name>.*` to select all of them.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Slot
 */
interface RangeItemSlot : ItemSlot, ClosedRange<Int>, ItemSlotWrapper {
	override fun asString() = all()

	/** The slot string selecting every slot of the range, like `armor.*`. */
	fun all() = "${name()}.*"

	@Deprecated("A range is already the slot selection of all its slots.", ReplaceWith("this"))
	fun range() = this
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
			in MOB.INVENTORY -> MOB.INVENTORY[index - MOB.INVENTORY.start]
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

/** A range of numbered slots, `hotbar.0` to `hotbar.8`. */
interface IndexedItemSlot : RangeItemSlot {
	/** Returns the [ItemSlotType] at the given index within the range. */
	operator fun get(index: Int) = ItemSlotType(start + index) { "${name()}.$index" }

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
private fun ItemSlotWrapper.subType(name: String, index: Int) = ItemSlotType(index) { "${name()}.$name" }

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

/** General container slots (0-53). See: https://minecraft.wiki/w/Slot */
val CONTAINER = IndexedItemSlot(0, 53) { "container" }

/** Used for item entities. */
val CONTENTS = ItemSlotType { "contents" }

/** Ender chest slots (200-226). */
val ENDERCHEST = IndexedItemSlot(200, 226) { "enderchest" }

/** Horse inventory slots (500-514). */
data object HORSE : IndexedItemSlot {
	override var start = 500
	override val endInclusive = start + 14

	override fun name() = "horse"

	/** The chest slot of the horse inventory. */
	val CHEST = subType("chest", 499)
}

/** Hotbar slots (0-8). */
val HOTBAR = IndexedItemSlot(0, 8) { "hotbar" }

/** Player inventory slots (9-35). */
val INVENTORY = IndexedItemSlot(9, 35) { "inventory" }

/** Mob-specific slots (villager, piglin, etc.). */
data object MOB : ItemSlotWrapper {
	override fun name() = "mob"

	/** The inventory slots of the mob (indices 300-307, slots 0-7). See: https://minecraft.wiki/w/Slot */
	val INVENTORY = IndexedItemSlot(300, 307) { "${name()}.inventory" }
}

/** Player-specific slots. */
data object PLAYER : ItemSlotWrapper {
	override fun name() = "player"

	/** The cursor slot of the player inventory. */
	val CURSOR = subType("cursor", 499)

	/** The crafting slots of the player inventory. */
	val CRAFTING = IndexedItemSlot(500, 503) { "${name()}.crafting" }
}

/** Saddle slot (400). */
val SADDLE = ItemSlotType(400) { "saddle" }

/** Weapon slots (mainhand: 98, offhand: 99), `weapon` alone being the mainhand and [all] both hands. */
data object WEAPON : ItemSlotType, RangeItemSlot {
	override val start = 98
	override val endInclusive = 99

	override fun asIndex() = 98
	override fun asString() = name()
	override fun name() = "weapon"

	/** The mainhand slot of the weapon inventory. */
	val MAINHAND = subType("mainhand", 98)

	/** The offhand slot of the weapon inventory. */
	val OFFHAND = subType("offhand", 99)
}
