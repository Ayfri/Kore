package arguments

fun interface ItemSlot : Argument

fun interface ItemSlotType : ItemSlot

fun interface IndexedItemSlot : ItemSlot {
	operator fun get(index: Int) = ItemSlotType { "${asString()}.$index" }
}

private fun ItemSlot.subType(name: String) = ItemSlotType { "${asString()}.$name" }

object ARMOR : ItemSlot {
	override fun asString() = "armor"

	val FEET = subType("feet")
	val LEGS = subType("legs")
	val CHEST = subType("chest")
	val HEAD = subType("head")
}

val CONTAINER = IndexedItemSlot { "container" }
val CRAFTING = IndexedItemSlot { "crafting" }
val ENDERCHEST = IndexedItemSlot { "enderchest" }

object HORSE : IndexedItemSlot {
	override fun asString() = "horse"

	val ARMOR = subType("armor")
	val CHEST = subType("chest")
	val SADDLE = subType("saddle")
}

val HOTBAR = IndexedItemSlot { "hotbar" }

object WEAPON : ItemSlotType {
	override fun asString() = "weapon"

	val MAINHAND = subType("mainhand")
	val OFFHAND = subType("offhand")
}

val VILLAGER = IndexedItemSlot { "villager" }
