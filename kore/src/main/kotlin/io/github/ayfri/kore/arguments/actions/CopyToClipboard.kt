package io.github.ayfri.kore.arguments.actions

import kotlinx.serialization.Serializable

@Serializable
data class CopyToClipboard(
	var value: String,
) : Action(), ClickEvent, DialogAction

/** Copy the given value to the clipboard. */
fun ActionWrapper<*>.copyToClipboard(value: String) = apply { action = CopyToClipboard(value) }
