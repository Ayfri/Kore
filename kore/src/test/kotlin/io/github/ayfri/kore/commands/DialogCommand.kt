package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.features.dialogs.types.dialogList
import io.github.ayfri.kore.features.dialogs.types.dialogs
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.Dialogs

fun Function.dialogTests() {
	dialogShow(self(), Dialogs.SERVER_LINKS) assertsIs "dialog show @s minecraft:server_links"
	dialogShow(self()) {
		dialogList(title="") {
			dialogs(Dialogs.SERVER_LINKS)
		}
	}
	dialogClear(self()) assertsIs "dialog clear @s"
}
