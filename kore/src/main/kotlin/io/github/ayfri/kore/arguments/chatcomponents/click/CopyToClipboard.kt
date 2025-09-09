package io.github.ayfri.kore.arguments.chatcomponents.click

import kotlinx.serialization.Serializable

@Serializable
data class CopyToClipboard(
	var value: String,
) : ClickEvent()

fun ClickEventContainer.copyToClipboard(value: String) = apply { event = CopyToClipboard(value)}
