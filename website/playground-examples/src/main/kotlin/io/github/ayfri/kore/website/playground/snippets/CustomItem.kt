package io.github.ayfri.kore.website.playground.snippets.customitem

import io.github.ayfri.kore.arguments.WEAPON
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.components.item.customData
import io.github.ayfri.kore.arguments.components.item.customName
import io.github.ayfri.kore.arguments.components.item.enchantment
import io.github.ayfri.kore.arguments.components.item.enchantments
import io.github.ayfri.kore.arguments.components.item.lore
import io.github.ayfri.kore.arguments.components.predicate
import io.github.ayfri.kore.arguments.types.literals.allPlayers
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.commands.effect
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.give
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.functions.tick
import io.github.ayfri.kore.generated.Effects
import io.github.ayfri.kore.generated.Enchantments
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.pack.pack
import io.github.ayfri.kore.utils.set

fun playground() = dataPack("arena_blade") {
	pack { description = textComponent("A custom item and the check that recognizes it") }

	val arenaBlade = Items.DIAMOND_SWORD {
		customName(textComponent("Arena Blade", Color.AQUA))
		lore("Forged for the arena")
		enchantments {
			enchantment(Enchantments.SHARPNESS, 5)
		}
		customData { this["arena_blade"] = true }
	}

	val heldBlade = Items.DIAMOND_SWORD.predicate {
		customData { this["arena_blade"] = true }
		setPartial(ItemComponentTypes.CUSTOM_DATA)
	}

	function("give_blade") {
		give(allPlayers(), arenaBlade)
	}

	tick("blade_strength") {
		execute {
			asTarget(allPlayers())
			ifCondition { items(self(), WEAPON.MAINHAND, heldBlade) }
			run {
				effect(self()) { give(Effects.STRENGTH, 1, 0, hideParticles = true) }
			}
		}
	}
}
