package io.github.ayfri.kore.arguments.actions

import kotlinx.serialization.Serializable

/** Copies [value] to the player's system clipboard when the component is clicked. */
@Serializable
data class CopyToClipboard(
	/** The text to copy to the clipboard. */
	var value: String,
) : Action(), ClickEvent, DialogAction

/** Copy the given value to the clipboard. */
fun ActionWrapper<*>.copyToClipboard(value: String) = apply { action = CopyToClipboard(value) }
