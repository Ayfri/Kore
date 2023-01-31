package items

import arguments.*
import commands.summon
import functions.Function
import nbt.MutableNbtCompound
import nbt.mutableNbt

data class ItemStack(val name: String, val namespace: String = "minecraft", val count: Int = 1, val nbtData: MutableNbtCompound = mutableNbt()) {
	init {
		require(count in 1..64) { "Count must be between 1 and 64" }

		nbtData["Item"] = nbt {
			this["id"] = item(name, namespace).asString()
			this["Count"] = count
			this["tag"] = nbt()
		}
	}

	fun asArgument() = Argument.Item(name, namespace, nbtData.toNbtCompound())
}

fun itemStack(name: String, namespace: String = "minecraft", count: Int = 1, nbtData: MutableNbtCompound = mutableNbt()) =
	ItemStack(name, namespace, count, nbtData)

context(Function)
fun ItemStack.summon(position: Vec3 = coordinate()) = summon("minecraft:item", position, nbtData.toNbtCompound())

context(Function)
fun ItemStack.summon(displayName: String, visible: Boolean = true) = summon("minecraft:item", coordinate(), nbtData.apply {
	this["CustomName"] = textComponent(displayName)
	this["CustomNameVisible"] = visible
}.toNbtCompound())

context(Function)
fun ItemStack.summon(displayName: TextComponents, visible: Boolean = true) = summon("minecraft:item", coordinate(), nbtData.apply {
	this["CustomName"] = displayName
	this["CustomNameVisible"] = visible
}.toNbtCompound())
