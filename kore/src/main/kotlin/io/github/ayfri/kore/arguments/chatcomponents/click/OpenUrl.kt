package io.github.ayfri.kore.arguments.chatcomponents.click

import kotlinx.serialization.Serializable

@Serializable
data class OpenUrl(
	var url: String,
) : ClickEvent()

fun ClickEventContainer.openUrl(url: String) = apply { event = OpenUrl(url) }
