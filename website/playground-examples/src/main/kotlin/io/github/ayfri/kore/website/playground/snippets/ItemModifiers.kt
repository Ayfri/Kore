package io.github.ayfri.kore.website.playground.snippets.itemmodifiers

import io.github.ayfri.kore.arguments.WEAPON
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.commands.items
import io.github.ayfri.kore.commands.tellraw
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.itemmodifiers.functions.enchantRandomly
import io.github.ayfri.kore.features.itemmodifiers.functions.setDamage
import io.github.ayfri.kore.features.itemmodifiers.functions.setName
import io.github.ayfri.kore.features.itemmodifiers.itemModifier
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.generated.Enchantments
import io.github.ayfri.kore.pack.pack

fun playground() = dataPack("forge") {
	pack { description = textComponent("Item modifiers applied from a function") }

	val reforge = itemModifier("reforge") {
		setName(textComponent("Reforged Blade", Color.AQUA))
		setDamage(1f)
		enchantRandomly(Enchantments.FIRE_ASPECT, Enchantments.KNOCKBACK, Enchantments.SHARPNESS)
	}

	function("reforge_held") {
		items { modify(self(), WEAPON.MAINHAND, reforge) }
		tellraw(self(), textComponent("Your weapon glows with new power", Color.LIGHT_PURPLE))
	}
}
