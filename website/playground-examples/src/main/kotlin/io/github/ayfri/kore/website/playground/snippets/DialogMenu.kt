package io.github.ayfri.kore.website.playground.snippets.dialogmenu

import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.commands.clear
import io.github.ayfri.kore.commands.give
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.helpers.menus.menu
import io.github.ayfri.kore.pack.pack

fun playground() = dataPack("kit_picker") {
	pack { description = textComponent("A kit picker built with the menu DSL") }

	val kits = menu("kits", "Choose your kit", Color.GOLD) {
		text("Your inventory is replaced by the kit.")

		button("Warrior") {
			clear(self())
			give(self(), Items.IRON_SWORD)
			give(self(), Items.SHIELD)
		}

		button("Archer") {
			clear(self())
			give(self(), Items.BOW)
			give(self(), Items.ARROW, 32)
		}

		page("Help") {
			text("Pick a new kit after each death.")
			link("Rules", "https://example.net/rules")
		}

		pauseScreen = true
	}

	function("open_kits") { kits.open() }
}
