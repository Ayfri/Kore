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

	fun asArgument() = item(name, namespace, nbtData.toNbtCompound())
}

fun itemStack(name: String, namespace: String = "minecraft", count: Int = 1, nbtData: MutableNbtCompound = mutableNbt()) =
	ItemStack(name, namespace, count, nbtData)

context(Function)
fun ItemStack.summon(position: Vec3 = coordinate()) = summon("minecraft:item", position, nbtData.toNbtCompound())
