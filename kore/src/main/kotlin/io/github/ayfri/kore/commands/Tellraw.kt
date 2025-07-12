package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.PlainTextComponent
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.functions.Function

fun Function.tellraw(targets: EntityArgument, text: String = "", block: PlainTextComponent.() -> Unit) =
	addLine(command("tellraw", targets, textComponent(text, block = block).asSnbtArg()))

fun Function.tellraw(targets: EntityArgument, message: ChatComponents) = addLine(command("tellraw", targets, message.asSnbtArg()))
fun Function.tellraw(targets: EntityArgument, message: String, color: Color? = null, block: PlainTextComponent.() -> Unit = {}) =
	addLine(command("tellraw", targets, textComponent(message, color, block).asSnbtArg()))
