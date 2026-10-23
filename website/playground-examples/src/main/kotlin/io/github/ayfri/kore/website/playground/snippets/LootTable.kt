package io.github.ayfri.kore.website.playground.snippets.loottable

import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.commands.loot
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.itemmodifiers.functions.setCount
import io.github.ayfri.kore.features.loottables.condition
import io.github.ayfri.kore.features.loottables.entries
import io.github.ayfri.kore.features.loottables.entries.item
import io.github.ayfri.kore.features.loottables.entries.modifier
import io.github.ayfri.kore.features.loottables.lootTable
import io.github.ayfri.kore.features.loottables.pool
import io.github.ayfri.kore.features.predicates.conditions.randomChance
import io.github.ayfri.kore.features.predicates.providers.constant
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.pack.pack

fun playground() = dataPack("treasure") {
	pack { description = textComponent("A loot table rolled from a function") }

	val bonusChest = lootTable("bonus_chest") {
		pool {
			rolls = constant(3)

			entries {
				item(Items.DIAMOND) {
					modifier {
						setCount(2)
					}
				}

				item(Items.EMERALD)
			}

			condition {
				randomChance(0.5f)
			}
		}
	}

	function("open_chest") {
		loot(self(), bonusChest)
	}
}
