package io.github.ayfri.kore.arguments.actions

import kotlinx.serialization.Serializable

/** Opens [url] in the system's default browser when the player clicks the component. Must use `http://` or `https://`. */
@Serializable
data class OpenUrl(
	/** The URL to open. Must be an `http://` or `https://` address. */
	var url: String,
) : Action(), ClickEvent, DialogAction

/** Opens the given url in the default browser. */
fun ActionWrapper<*>.openUrl(url: String) = apply { action = OpenUrl(url) }
