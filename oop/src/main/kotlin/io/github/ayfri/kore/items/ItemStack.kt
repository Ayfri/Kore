package io.github.ayfri.kore.items

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.maths.coordinate
import io.github.ayfri.kore.commands.summon
import io.github.ayfri.kore.data.item.ItemStack
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.nbt.MutableNbtCompound
import io.github.ayfri.kore.nbt.mutableNbt
import io.github.ayfri.kore.nbt.toMutableNbtCompound

fun itemStack(name: String, namespace: String = "minecraft", count: Int = 1, nbtData: MutableNbtCompound = mutableNbt()) =
	ItemStack("$namespace:$name", count.toShort(), nbtData.toNbtCompound())

context(Function)
fun ItemStack.summon(position: Vec3 = coordinate()) = summon(io.github.ayfri.kore.generated.EntityTypes.ITEM, position, tag)

context(Function)
fun ItemStack.summon(displayName: ChatComponents, visible: Boolean = true) = summon(
	io.github.ayfri.kore.generated.EntityTypes.ITEM,
	coordinate(),
	tag?.toMutableNbtCompound()?.apply {
		this["CustomName"] = displayName
		this["CustomNameVisible"] = visible
	}?.toNbtCompound()
)

context(Function)
fun ItemStack.summon(displayName: String, color: Color, visible: Boolean = true) = summon(textComponent(displayName, color), visible)
