package helpers

import arguments.chatcomponents.textComponent
import arguments.vec3
import commands.AttributeModifierOperation
import commands.summon
import data.block.properties
import data.item.builders.canDestroy
import data.item.builders.enchantments
import data.item.builders.modifiers
import functions.Function
import generated.Attributes
import generated.Blocks
import generated.Enchantments
import generated.Items
import helpers.displays.*
import helpers.displays.entities.ItemDisplayModelMode
import helpers.displays.maths.*

fun Function.displayTests() {
	val itemDisplay = itemDisplay {
		item(Items.DIAMOND_SWORD) {
			name = textComponent("test")

			enchantments {
				Enchantments.SHARPNESS at 1
				Enchantments.UNBREAKING at 3
			}

			modifiers {
				modifier(Attributes.GENERIC_ATTACK_DAMAGE, 1.0, AttributeModifierOperation.ADD)
			}

			customModelData = 1

			canDestroy(Blocks.DIRT)
		}

		displayMode = ItemDisplayModelMode.HEAD

		transformation {
			leftRotation {
				quaternionNormalized(0.191, 0.038, -0.683, 0.704)
			}

			rightRotation {
				quaternionNormalized(0.274, -0.169, -0.080, 0.944)
			}

			scale {
				x = -1f
				y = -1f
				z = 0.21f
			}

			translation {
				x = 0.31f
				y = -0.24f
				z = 0.1f
			}
		}
	}

	summon(itemDisplay.entityType, vec3(), itemDisplay.toNbt())

	val blockDisplay = blockDisplay {
		blockState(Blocks.GRASS_BLOCK) {
			properties {
				put("snowy", "true")
			}
		}
	}

	summon(blockDisplay.entityType, vec3(), blockDisplay.toNbt())

	val textDisplay = textDisplay {
		text("test")

		interpolationDuration = 1
	}

	summon(textDisplay.entityType, vec3(), textDisplay.toNbt())
}
