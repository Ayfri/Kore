package io.github.ayfri.kore.arguments.chatcomponents.events

import io.github.ayfri.kore.commands.Command
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.emptyFunction
import io.github.ayfri.kore.serializers.LowercaseSerializer
import io.github.ayfri.kore.utils.asArg
import io.github.ayfri.kore.utils.set
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.buildNbtCompound
import java.io.File
import java.net.URL

@Serializable
data class ClickEvent(
	var action: ClickAction,
	var command: String? = null,
	var page: Int? = null,
	var url: String? = null,
	var value: String? = null, // For other actions like copy_to_clipboard, open_file
) {
	fun toNbtTag() = buildNbtCompound {
		this["action"] = action.asArg()
		when (action) {
			ClickAction.OPEN_URL -> url?.let { this["url"] = it }
			ClickAction.RUN_COMMAND, ClickAction.SUGGEST_COMMAND -> command?.let { this["command"] = it }
			ClickAction.CHANGE_PAGE -> page?.let { this["page"] = it }
			else -> value?.let { this["value"] = it }
		}
	}
}

fun ClickEvent.changePage(page: Int) = apply {
	action = ClickAction.CHANGE_PAGE
	this.page = page
	command = null
	url = null
	value = null
}

fun ClickEvent.copyToClipboard(text: String) = apply {
	action = ClickAction.COPY_TO_CLIPBOARD
	command = null
	page = null
	url = null
	value = text
}

@Deprecated("Can't be used by datapacks.")
fun ClickEvent.openFile(path: String) = apply {
	action = ClickAction.OPEN_FILE
	command = null
	page = null
	url = null
	value = path
}

@Deprecated("Can't be used by datapacks.")
fun ClickEvent.openFile(path: File) = apply {
	action = ClickAction.OPEN_FILE
	command = null
	page = null
	url = null
	value = path.toString()
}

fun ClickEvent.openUrl(url: String) = apply {
	action = ClickAction.OPEN_URL
	command = null
	page = null
	this.url = url
	value = null
}

fun ClickEvent.openUrl(url: URL) = apply {
	action = ClickAction.OPEN_URL
	this.url = url.toString()
	command = null
	page = null
	value = null
}

fun ClickEvent.runCommand(block: Function.() -> Command) = apply {
	action = ClickAction.RUN_COMMAND
	command = emptyFunction().block().toString()
	page = null
	url = null
	value = null
}

fun ClickEvent.suggestCommand(block: Function.() -> Command) = apply {
	action = ClickAction.SUGGEST_COMMAND
	command = "/${emptyFunction().block()}"
	page = null
	url = null
	value = null
}

fun ClickEvent.suggestChatMessage(message: String) = apply {
	action = ClickAction.SUGGEST_COMMAND
	command = message
	page = null
	url = null
	value = null
}

@Serializable(ClickAction.Companion.ClickActionSerializer::class)
enum class ClickAction {
	CHANGE_PAGE,
	COPY_TO_CLIPBOARD,

	@Deprecated("Can't be used by datapacks.")
	OPEN_FILE,
	OPEN_URL,
	RUN_COMMAND,

	@Deprecated("Use SUGGEST_COMMAND instead.")
	@SerialName("/")
	SLASH,
	SUGGEST_COMMAND;

	companion object {
		data object ClickActionSerializer : LowercaseSerializer<ClickAction>(entries)
	}
}
