package arguments

fun interface SlotType {
	fun name(): String
}

fun interface SlotEntry : SlotType

fun interface IndexedSlotType : SlotType {
	operator fun get(index: Int) = SlotEntry { "${name()}.$index" }
}

private fun SlotType.subEntry(name: String) = SlotEntry { "${name()}.$name" }

object ARMOR : SlotType {
	override fun name() = "armor"
	
	val FEET = subEntry("feet")
	val LEGS = subEntry("legs")
	val CHEST = subEntry("chest")
	val HEAD = subEntry("head")
}

val CONTAINER = IndexedSlotType { "container" }
val CRAFTING = IndexedSlotType { "crafting" }
val ENDERCHEST = IndexedSlotType { "enderchest" }

object HORSE : IndexedSlotType {
	override fun name() = "horse"
	
	val ARMOR = subEntry("armor")
	val CHEST = subEntry("chest")
	val SADDLE = subEntry("saddle")
}

val HOTBAR = IndexedSlotType { "hotbar" }

object WEAPON : SlotEntry {
	override fun name() = "weapon"
	
	val MAINHAND = subEntry("mainhand")
	val OFFHAND = subEntry("offhand")
}

data class Slot(val type: SlotEntry) : Argument {
	override fun asString() = type.name()
}

fun slot(type: SlotEntry) = Slot(type)
