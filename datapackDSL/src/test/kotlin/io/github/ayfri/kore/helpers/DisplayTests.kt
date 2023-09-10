package io.github.ayfri.kore.helpers

import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.numbers.seconds
import io.github.ayfri.kore.arguments.types.literals.allEntities
import io.github.ayfri.kore.commands.AttributeModifierOperation
import io.github.ayfri.kore.commands.kill
import io.github.ayfri.kore.commands.schedule
import io.github.ayfri.kore.commands.summon
import io.github.ayfri.kore.data.block.properties
import io.github.ayfri.kore.data.item.builders.canDestroy
import io.github.ayfri.kore.data.item.builders.enchantments
import io.github.ayfri.kore.data.item.builders.modifiers
import io.github.ayfri.kore.features.predicates.providers.constant
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.Attributes
import io.github.ayfri.kore.generated.Blocks
import io.github.ayfri.kore.generated.Enchantments
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.helpers.displays.*
import io.github.ayfri.kore.helpers.displays.entities.ItemDisplayModelMode
import io.github.ayfri.kore.helpers.displays.maths.*

fun Function.displayTests() {
	val itemDisplay = itemDisplay {
		item(Items.DIAMOND_SWORD) {
			name = textComponent("test")

			enchantments {
				Enchantments.SHARPNESS at 1
				Enchantments.UNBREAKING at 3
			}

			modifiers {
				modifier(Attributes.GENERIC_ATTACK_DAMAGE, constant(1f), AttributeModifierOperation.ADD)
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

	kill(allEntities { type = itemDisplay.entityType })
	kill(allEntities { type = blockDisplay.entityType })
	kill(allEntities { type = textDisplay.entityType })

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
