package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.features.dialogs.Dialogs
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.arguments.types.DialogArgument
import io.github.ayfri.kore.utils.snbtSerializer
import kotlinx.serialization.encodeToString
import io.github.ayfri.kore.dataPack as dpFunction

fun Function.dialogShow(targets: EntityArgument, dialog: DialogArgument) =
	addLine(command("dialog", literal("show"), targets, dialog))

fun Function.dialogShow(targets: EntityArgument, block: Dialogs.() -> DialogArgument): Command {
	val tempDp = dpFunction( "") {}
	Dialogs(tempDp).block()
	if (tempDp.dialogs.isEmpty()) error("Please provide at least one dialog to show.")
	return addLine(command("dialog", literal("show"), targets, literal(snbtSerializer.encodeToString(tempDp.dialogs.first()))))
}

fun Function.dialogClear(targets: EntityArgument) = addLine(command("dialog", literal("clear"), targets))
