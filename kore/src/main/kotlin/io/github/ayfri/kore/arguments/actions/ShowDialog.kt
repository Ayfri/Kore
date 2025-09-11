package io.github.ayfri.kore.arguments.actions

import io.github.ayfri.kore.generated.arguments.types.DialogArgument
import kotlinx.serialization.Serializable

@Serializable
data class ShowDialog(
	var dialog: DialogArgument,
) : Action()

fun ActionWrapper.showDialog(dialog: DialogArgument) = apply { action = ShowDialog(dialog) }
