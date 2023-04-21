package helpers.displays

import arguments.Argument
import arguments.Color
import arguments.chatcomponents.PlainTextComponent
import data.block.BlockStateBuilder
import data.item.builders.ItemStackBuilder
import data.item.builders.itemStack
import helpers.displays.entities.BlockDisplay
import helpers.displays.entities.ItemDisplay
import helpers.displays.entities.TextDisplay

fun itemDisplay(block: ItemDisplay.() -> Unit) = ItemDisplay().apply(block)

fun ItemDisplay.item(item: Argument.Item, count: Short = 1, block: ItemStackBuilder.() -> Unit) {
	this.item = itemStack(item, count, block)
}

fun blockDisplay(block: BlockDisplay.() -> Unit) = BlockDisplay().apply(block)

fun BlockDisplay.blockState(block: Argument.Block, init: BlockStateBuilder.() -> Unit) {
	blockState = data.block.blockState(block, init)
}

fun TextDisplay.text(text: String = "", init: PlainTextComponent.() -> Unit = {}) {
	this.text = arguments.chatcomponents.text(text, init)
}

fun TextDisplay.text(text: String = "", color: Color, init: PlainTextComponent.() -> Unit = {}) {
	this.text = arguments.chatcomponents.text(text, color, init)
}

fun textDisplay(block: TextDisplay.() -> Unit) = TextDisplay().apply(block)
