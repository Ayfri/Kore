package items

import arguments.Coordinate
import arguments.item
import commands.summon
import functions.Function
import net.benwoodworth.knbt.NbtCompound

data class ItemStack(val name: String, val namespace: String = "minecraft", val count: Int = 1, val nbtData: NbtCompound? = null) {
	fun asArgument() = item(name, namespace, nbtData)
}

fun itemStack(name: String, namespace: String = "minecraft", count: Int = 1, nbtData: NbtCompound? = null) = ItemStack(name, namespace, count, nbtData)

context(Function)
fun ItemStack.summon(position: Coordinate? = null) = summon("minecraft:item", position, nbtData)
