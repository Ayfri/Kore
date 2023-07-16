package helpers

import arguments.chatcomponents.textComponent
import arguments.maths.vec3
import arguments.numbers.seconds
import arguments.types.literals.allEntities
import commands.AttributeModifierOperation
import commands.kill
import commands.schedule
import commands.summon
import data.block.properties
import data.item.builders.canDestroy
import data.item.builders.enchantments
import data.item.builders.modifiers
import features.predicates.providers.constant
import functions.Function
import generated.*
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
				modifier(Attributes.GENERIC_ATTACK_DAMAGE, constant(1f), AttributeModifierOperation.ADDITION)
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
				this["snowy"] = true
			}
		}
	}

	summon(blockDisplay.entityType, vec3(), blockDisplay.toNbt())

	val textDisplay = textDisplay {
		text("test")
		interpolationDuration = 1
	}

	summon(textDisplay.entityType, vec3(), textDisplay.toNbt())

	kill(allEntities { type(itemDisplay.entityType) })
	kill(allEntities { type(blockDisplay.entityType) })
	kill(allEntities { type(textDisplay.entityType) })

	val simpleInterpolationEntity = blockDisplay {
		blockState(Blocks.STONE)
	}.interpolable(vec3(0, -59, 0))

	simpleInterpolationEntity.summon()

	schedule(1.seconds) {
		simpleInterpolationEntity.interpolateTo(1.5.seconds) {
			scale = Vec3f(1f, 1f, 1f)
		}

		schedule(3.seconds) {
			simpleInterpolationEntity.interpolateTo(1.5.seconds) {
				scale = Vec3f(0.5f, 0.5f, 0.5f)
			}
		}

		schedule(this).replace(6.seconds)
	}
}
