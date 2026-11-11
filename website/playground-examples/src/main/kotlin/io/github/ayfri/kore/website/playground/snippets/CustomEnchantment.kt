package io.github.ayfri.kore.website.playground.snippets.customenchantment

import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.components.data.EquipmentSlot
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.enchantments.damage
import io.github.ayfri.kore.features.enchantments.description
import io.github.ayfri.kore.features.enchantments.effects
import io.github.ayfri.kore.features.enchantments.effects.builders.PostAttackSpecifier
import io.github.ayfri.kore.features.enchantments.effects.builders.add
import io.github.ayfri.kore.features.enchantments.effects.builders.ignite
import io.github.ayfri.kore.features.enchantments.effects.builders.on
import io.github.ayfri.kore.features.enchantments.enchantment
import io.github.ayfri.kore.features.enchantments.exclusiveSet
import io.github.ayfri.kore.features.enchantments.maxCost
import io.github.ayfri.kore.features.enchantments.minCost
import io.github.ayfri.kore.features.enchantments.postAttack
import io.github.ayfri.kore.features.enchantments.primaryItems
import io.github.ayfri.kore.features.enchantments.slots
import io.github.ayfri.kore.features.enchantments.supportedItems
import io.github.ayfri.kore.features.enchantments.values.linearLevelBased
import io.github.ayfri.kore.generated.Enchantments
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.generated.Tags
import io.github.ayfri.kore.pack.pack

fun playground() = dataPack("fire_aspect_plus") {
	pack { description = textComponent("A sword enchantment that burns and hits harder") }

	enchantment("fire_aspect_plus") {
		description("Fire Aspect+")
		supportedItems(Items.DIAMOND_SWORD, Items.NETHERITE_SWORD)
		primaryItems(Tags.Item.SWORDS)
		exclusiveSet(Enchantments.FIRE_ASPECT)
		weight = 2
		maxLevel = 3
		minCost(15, 10)
		maxCost(65, 10)
		anvilCost = 4
		slots(EquipmentSlot.MAINHAND)

		effects {
			damage {
				add(linearLevelBased(2f, 0.5f))
			}

			postAttack {
				on(PostAttackSpecifier.ATTACKER, PostAttackSpecifier.VICTIM) {
					ignite(5)
				}
			}
		}
	}
}
