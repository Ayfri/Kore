package io.github.ayfri.kore.arguments.actions

import kotlinx.serialization.Serializable

@Serializable
data class CopyToClipboard(
	var value: String,
) : ClickEvent()

fun ActionWrapper<*>.copyToClipboard(value: String) = apply { action = CopyToClipboard(value)}
