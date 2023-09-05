package items

import arguments.chatcomponents.ChatComponents
import arguments.chatcomponents.textComponent
import arguments.colors.Color
import arguments.maths.Vec3
import arguments.maths.coordinate
import commands.summon
import data.item.ItemStack
import functions.Function
import generated.EntityTypes
import nbt.MutableNbtCompound
import nbt.mutableNbt
import nbt.toMutableNbtCompound

fun itemStack(name: String, namespace: String = "minecraft", count: Int = 1, nbtData: MutableNbtCompound = mutableNbt()) =
	ItemStack("$namespace:$name", count.toShort(), nbtData.toNbtCompound())

context(Function)
fun ItemStack.summon(position: Vec3 = coordinate()) = summon(EntityTypes.ITEM, position, tag)

context(Function)
fun ItemStack.summon(displayName: ChatComponents, visible: Boolean = true) = summon(
	EntityTypes.ITEM,
	coordinate(),
	tag?.toMutableNbtCompound()?.apply {
		this["CustomName"] = displayName
		this["CustomNameVisible"] = visible
	}?.toNbtCompound()
)

context(Function)
fun ItemStack.summon(displayName: String, color: Color, visible: Boolean = true) = summon(textComponent(displayName, color), visible)
