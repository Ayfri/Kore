package io.github.ayfri.kore.helpers.conversations.utils

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.translatedTextComponent

/**
 * This text as said by [speaker] in the vanilla chat look, `<Bob> Hello`, or as is without a speaker.
 *
 * ```
 * textComponent("Hello").saidBy(textComponent("Bob"))  // {translate:"chat.type.text",with:["Bob","Hello"]}, as a translatable component
 * textComponent("Hello").saidBy(null)                  // "Hello"
 * ```
 */
internal fun ChatComponents.saidBy(speaker: ChatComponents?) =
	speaker?.let { translatedTextComponent("chat.type.text", listOf(it, this)) } ?: this
