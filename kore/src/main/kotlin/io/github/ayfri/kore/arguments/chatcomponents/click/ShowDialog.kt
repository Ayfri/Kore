package io.github.ayfri.kore.arguments.chatcomponents.click

import io.github.ayfri.kore.generated.arguments.types.DialogArgument
import kotlinx.serialization.Serializable

@Serializable
data class ShowDialog(
	var dialog: DialogArgument,
) : ClickEvent()

fun ClickEventContainer.showDialog(dialog: DialogArgument) = apply { event = ShowDialog(dialog) }
