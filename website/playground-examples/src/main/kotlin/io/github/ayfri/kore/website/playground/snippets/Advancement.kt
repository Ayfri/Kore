package io.github.ayfri.kore.website.playground.snippets.advancement

import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.advancements.AdvancementFrameType
import io.github.ayfri.kore.features.advancements.advancement
import io.github.ayfri.kore.features.advancements.criteria
import io.github.ayfri.kore.features.advancements.display
import io.github.ayfri.kore.features.advancements.rewards
import io.github.ayfri.kore.features.advancements.triggers.inventoryChanged
import io.github.ayfri.kore.features.advancements.triggers.items
import io.github.ayfri.kore.features.predicates.sub.itemStackPredicate
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.pack.pack

fun playground() = dataPack("advancement_demo") {
	pack { description = textComponent("Advancement demo") }

	advancement("sharp_start") {
		display(Items.DIAMOND_SWORD, "Sharp start", "Hold a diamond sword") {
			frame = AdvancementFrameType.CHALLENGE
		}

		criteria {
			inventoryChanged("has_sword") {
				items { add(itemStackPredicate(Items.DIAMOND_SWORD)) }
			}
		}

		rewards {
			experience = 50
		}
	}
}
