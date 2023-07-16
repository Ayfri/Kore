package arguments.chatcomponents.events

import commands.Command
import functions.Function
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.buildNbtCompound
import serializers.LowercaseSerializer
import utils.asArg
import utils.set
import java.io.File
import java.net.URL

@Serializable
data class ClickEvent(
	var action: ClickAction,
	var value: String,
) {
	fun toNbtTag() = buildNbtCompound {
		this["action"] = action.asArg()
		this["value"] = value
	}
}

fun ClickEvent.changePage(page: Int) = apply {
	action = ClickAction.CHANGE_PAGE
	value = page.toString()
}

fun ClickEvent.copyToClipboard(text: String) = apply {
	action = ClickAction.COPY_TO_CLIPBOARD
	value = text
}

fun ClickEvent.openFile(path: String) = apply {
	action = ClickAction.OPEN_FILE
	value = path
}

fun ClickEvent.openFile(path: File) = apply {
	action = ClickAction.OPEN_FILE
	value = path.toString()
}

fun ClickEvent.openUrl(url: String) = apply {
	action = ClickAction.OPEN_URL
	value = url
}

fun ClickEvent.openUrl(url: URL) = apply {
	action = ClickAction.OPEN_URL
	value = url.toString()
}

fun ClickEvent.runCommand(block: Function.() -> Command) = apply {
	action = ClickAction.RUN_COMMAND
	value = "/${Function.EMPTY.block()}"
}

fun ClickEvent.suggestCommand(block: Function.() -> Command) = apply {
	action = ClickAction.SUGGEST_COMMAND
	value = "/${Function.EMPTY.block()}"
}

@Serializable(ClickAction.Companion.ClickActionSerializer::class)
enum class ClickAction {
	CHANGE_PAGE,
	COPY_TO_CLIPBOARD,
	OPEN_FILE,
	OPEN_URL,
	RUN_COMMAND,

	@SerialName("/")
	SLASH,
	SUGGEST_COMMAND;

	companion object {
		data object ClickActionSerializer : LowercaseSerializer<ClickAction>(entries)
	}
}
