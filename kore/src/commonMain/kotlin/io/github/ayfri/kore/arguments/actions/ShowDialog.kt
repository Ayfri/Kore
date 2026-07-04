package io.github.ayfri.kore.arguments.actions

import io.github.ayfri.kore.generated.arguments.types.DialogArgument
import kotlinx.serialization.Serializable

/** Opens the [dialog] UI overlay when the player clicks the component. */
@Serializable
data class ShowDialog(
	/** The dialog resource to display. */
	var dialog: DialogArgument,
) : Action(), ClickEvent, DialogAction

/** Shows a dialog. */
fun ActionWrapper<*>.showDialog(dialog: DialogArgument) = apply { action = ShowDialog(dialog) }
