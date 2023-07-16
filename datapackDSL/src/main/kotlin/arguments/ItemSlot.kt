package arguments

interface ItemSlot : Argument

interface ItemSlotType : ItemSlot {
	fun asIndex(): Int

	companion object {
		operator fun invoke(index: Int = 0, block: () -> String) = object : ItemSlotType {
			override fun asString() = block()
			override fun asIndex() = index
		}

		fun fromIndex(index: Int, fromEntity: Boolean = false) = when (index) {
			-106 -> WEAPON.OFFHAND
			in 0..53 -> when {
				fromEntity && index in 0..8 -> HOTBAR[index]
				fromEntity && index in 9..35 -> INVENTORY[index - 9]
				else -> CONTAINER[index]
			}

			98 -> WEAPON
			99 -> WEAPON.OFFHAND
			100 -> ARMOR.FEET
			101 -> ARMOR.LEGS
			102 -> ARMOR.CHEST
			103 -> ARMOR.HEAD
			in 200..226 -> ENDERCHEST[index - 200]
			in 300..307 -> VILLAGER[index - 300]
			400 -> HORSE.SADDLE
			401 -> HORSE.ARMOR
			499 -> HORSE.CHEST
			in 500..514 -> HORSE[index - 500]
			else -> throw IllegalArgumentException("Invalid slot index: $index")
		}
	}
}

interface IndexedItemSlot : ItemSlot {
	var startIndex: Int

	operator fun get(index: Int) = ItemSlotType { "${asString()}.$index" }

	companion object {
		operator fun invoke(startIndex: Int, block: () -> String) = object : IndexedItemSlot {
			override fun asString() = block()
			override var startIndex = startIndex
		}
	}
}

private fun ItemSlot.subType(name: String, index: Int) = ItemSlotType(index) { "${asString()}.$name" }

data object ARMOR : ItemSlot {
	override fun asString() = "armor"

	val FEET = subType("feet", 100)
	val LEGS = subType("legs", 101)
	val CHEST = subType("chest", 102)
	val HEAD = subType("head", 103)
}

val CONTAINER = IndexedItemSlot(0) { "container" }
val ENDERCHEST = IndexedItemSlot(200) { "enderchest" }

data object HORSE : IndexedItemSlot {
	override var startIndex = 500

	override fun asString() = "horse"

	val ARMOR = subType("armor", 401)
	val CHEST = subType("chest", 499)
	val SADDLE = subType("saddle", 400)
}

val HOTBAR = IndexedItemSlot(0) { "hotbar" }
val INVENTORY = IndexedItemSlot(9) { "inventory" }

data object WEAPON : ItemSlotType {
	override fun asIndex() = 98

	override fun asString() = "weapon"

	val MAINHAND = subType("mainhand", 98)
	val OFFHAND = subType("offhand", 99)
}

val VILLAGER = IndexedItemSlot(300) { "villager" }
