package helpers.displays

import arguments.chatcomponents.PlainTextComponent
import arguments.colors.Color
import arguments.types.resources.BlockArgument
import arguments.types.resources.ItemArgument
import data.block.BlockStateBuilder
import data.item.builders.ItemStackBuilder
import data.item.builders.itemStack
import helpers.displays.entities.BlockDisplay
import helpers.displays.entities.ItemDisplay
import helpers.displays.entities.TextDisplay

fun itemDisplay(block: ItemDisplay.() -> Unit) = ItemDisplay().apply(block)

fun ItemDisplay.item(item: ItemArgument, count: Short = 1, block: ItemStackBuilder.() -> Unit = {}) {
	this.item = itemStack(item, count, block)
}

fun blockDisplay(block: BlockDisplay.() -> Unit) = BlockDisplay().apply(block)

fun BlockDisplay.blockState(block: BlockArgument, init: BlockStateBuilder.() -> Unit = {}) {
	blockState = data.block.blockState(block, init)
}

fun TextDisplay.text(text: String = "", color: Color? = null, init: PlainTextComponent.() -> Unit = {}) {
	this.text = arguments.chatcomponents.text(text, color, init)
}

fun textDisplay(block: TextDisplay.() -> Unit) = TextDisplay().apply(block)
