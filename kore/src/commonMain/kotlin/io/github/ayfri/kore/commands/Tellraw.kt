package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.PlainTextComponent
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.functions.Function

/** Sends a text component built from [text] and [block] to [targets]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/tellraw) */
fun Function.tellraw(targets: EntityArgument, text: String = "", block: PlainTextComponent.() -> Unit) =
	addLine(command("tellraw", targets, textComponent(text, block = block).asSnbtArg()))

/** Sends [message] as a JSON chat component to [targets]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/tellraw) */
fun Function.tellraw(targets: EntityArgument, message: ChatComponents) = addLine(command("tellraw", targets, message.asSnbtArg()))
/** Sends [message] to [targets], optionally tinting the base text with [color]. @see [Minecraft wiki](https://minecraft.wiki/w/Commands/tellraw) */
fun Function.tellraw(targets: EntityArgument, message: String, color: Color? = null, block: PlainTextComponent.() -> Unit = {}) =
	addLine(command("tellraw", targets, textComponent(message, color, block).asSnbtArg()))
