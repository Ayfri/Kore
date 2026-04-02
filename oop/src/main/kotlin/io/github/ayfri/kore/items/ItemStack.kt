package io.github.ayfri.kore.items

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.components.Components
import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.maths.coordinate
import io.github.ayfri.kore.commands.summon
import io.github.ayfri.kore.data.item.ItemStack
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.set

context(fn: Function)
fun ItemStack.summon(position: Vec3 = coordinate()) = fn.summon(EntityTypes.ITEM, position) {
	nbt {
		this["Item"] = nbt {
			this["components"] = components ?: Components()
		}
	}
}

context(fn: Function)
fun ItemStack.summon(displayName: ChatComponents, visible: Boolean = true) = fn.summon(
	EntityTypes.ITEM,
	coordinate(),
	nbt {
		this["Item"] = nbt {
			this["components"] = components ?: Components()
			this["CustomName"] = displayName
			this["CustomNameVisible"] = visible
		}
	}
)

context(fn: Function)
fun ItemStack.summon(displayName: String, color: Color, visible: Boolean = true) =
	summon(textComponent(displayName, color), visible)
