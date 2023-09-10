package io.github.ayfri.kore.helpers.displays

import io.github.ayfri.kore.arguments.chatcomponents.PlainTextComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.data.block.BlockStateBuilder
import io.github.ayfri.kore.data.item.builders.ItemStackBuilder
import io.github.ayfri.kore.data.item.builders.itemStack
import io.github.ayfri.kore.helpers.displays.entities.BlockDisplay
import io.github.ayfri.kore.helpers.displays.entities.ItemDisplay
import io.github.ayfri.kore.helpers.displays.entities.TextDisplay

fun itemDisplay(block: ItemDisplay.() -> Unit) = ItemDisplay().apply(block)

fun ItemDisplay.item(item: ItemArgument, count: Short = 1, block: ItemStackBuilder.() -> Unit = {}) {
	this.item = itemStack(item, count, block)
}

fun blockDisplay(block: BlockDisplay.() -> Unit) = BlockDisplay().apply(block)

fun BlockDisplay.blockState(block: BlockArgument, init: BlockStateBuilder.() -> Unit = {}) {
	blockState = io.github.ayfri.kore.data.block.blockState(block, init)
}

fun TextDisplay.text(text: String = "", color: Color? = null, init: PlainTextComponent.() -> Unit = {}) {
	this.text = io.github.ayfri.kore.arguments.chatcomponents.text(text, color, init)
}

fun textDisplay(block: TextDisplay.() -> Unit) = TextDisplay().apply(block)
