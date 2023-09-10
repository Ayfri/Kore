package io.github.ayfri.kore.arguments.chatcomponents.events

import io.github.ayfri.kore.commands.Command
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.emptyFunction
import io.github.ayfri.kore.serializers.LowercaseSerializer
import io.github.ayfri.kore.utils.asArg
import io.github.ayfri.kore.utils.set
import net.benwoodworth.knbt.buildNbtCompound
import java.io.File
import java.net.URL
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
	value = "/${emptyFunction().block()}"
}

fun ClickEvent.suggestCommand(block: Function.() -> Command) = apply {
	action = ClickAction.SUGGEST_COMMAND
	value = "/${emptyFunction().block()}"
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
