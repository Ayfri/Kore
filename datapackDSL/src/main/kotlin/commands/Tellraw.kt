package commands

import arguments.chatcomponents.ChatComponents
import arguments.chatcomponents.PlainTextComponent
import arguments.chatcomponents.textComponent
import arguments.colors.Color
import arguments.types.EntityArgument
import functions.Function

fun Function.tellraw(targets: EntityArgument, text: String = "", block: PlainTextComponent.() -> Unit) =
	addLine(command("tellraw", targets, textComponent(text, block = block).asJsonArg()))

fun Function.tellraw(targets: EntityArgument, message: ChatComponents) = addLine(command("tellraw", targets, message.asJsonArg()))
fun Function.tellraw(targets: EntityArgument, message: String, color: Color? = null, block: PlainTextComponent.() -> Unit = {}) =
	addLine(command("tellraw", targets, textComponent(message, color, block).asJsonArg()))
