package io.github.ayfri.kore.helpers

import io.github.ayfri.kore.arguments.components.item.*
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.numbers.seconds
import io.github.ayfri.kore.arguments.types.literals.UUIDArgument
import io.github.ayfri.kore.arguments.types.literals.allEntities
import io.github.ayfri.kore.commands.AttributeModifierOperation
import io.github.ayfri.kore.commands.kill
import io.github.ayfri.kore.commands.schedule
import io.github.ayfri.kore.commands.summon
import io.github.ayfri.kore.data.block.properties
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.entities.display.BlockDisplayEntity
import io.github.ayfri.kore.entities.display.ItemDisplayEntity
import io.github.ayfri.kore.entities.display.TextDisplayEntity
import io.github.ayfri.kore.entities.kill
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.generate
import io.github.ayfri.kore.generated.Attributes
import io.github.ayfri.kore.generated.Blocks
import io.github.ayfri.kore.generated.Enchantments
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.helpers.assertions.assertsIs
import io.github.ayfri.kore.helpers.displays.*
import io.github.ayfri.kore.helpers.displays.entities.ItemDisplayModelMode
import io.github.ayfri.kore.helpers.displays.maths.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldMatch
import kotlin.uuid.Uuid

fun Function.displayTests() {
	val itemDisplay = itemDisplay {
		item(Items.DIAMOND_SWORD) {
			customName("test")

			enchantments {
				enchantment(Enchantments.SHARPNESS, 1)
				enchantment(Enchantments.UNBREAKING, 3)
			}

			attributeModifiers {
				modifier(
					Attributes.ATTACK_DAMAGE,
					name = "test",
					namespace = datapack.name,
					amount = 1.0,
					operation = AttributeModifierOperation.ADD_VALUE,
				)
			}

			canBreak(Blocks.DIRT)
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

fun Function.displayEntitySelectorTests() {
	val uuid = UUIDArgument(Uuid.parse("00000000-0000-0000-0000-000000000001"))

	val blockEntity = BlockDisplayEntity(uuid)
	blockEntity.kill() assertsIs "kill @e[limit=1,nbt={UUID:[I;0,0,0,1]},type=minecraft:block_display]"

	val itemEntity = ItemDisplayEntity(uuid)
	itemEntity.kill() assertsIs "kill @e[limit=1,nbt={UUID:[I;0,0,0,1]},type=minecraft:item_display]"

	val textEntity = TextDisplayEntity(uuid)
	textEntity.kill() assertsIs "kill @e[limit=1,nbt={UUID:[I;0,0,0,1]},type=minecraft:text_display]"
}

fun Function.displayToEntityTests() {
	val blockInterpolable = blockDisplay {
		blockState(Blocks.STONE)
	}.interpolable(vec3(0, 0, 0))

	blockInterpolable.summon()
	blockInterpolable.toEntity().kill()
	lines[1] shouldMatch Regex("""kill @e\[limit=1,nbt=\{UUID:\[I;.*?\]\},type=minecraft:block_display\]""")

	val itemInterpolable = itemDisplay {
		item(Items.DIAMOND)
	}.interpolable(vec3(0, 0, 0))

	itemInterpolable.summon()
	itemInterpolable.toEntity().kill()
	lines[3] shouldMatch Regex("""kill @e\[limit=1,nbt=\{UUID:\[I;.*?\]\},type=minecraft:item_display\]""")

	val textInterpolable = textDisplay {
		text("hello")
	}.interpolable(vec3(0, 0, 0))

	textInterpolable.summon()
	textInterpolable.toEntity().kill()
	lines[5] shouldMatch Regex("""kill @e\[limit=1,nbt=\{UUID:\[I;.*?\]\},type=minecraft:text_display\]""")
}

class DisplayTests : FunSpec({
	test("display") {
		dataPack("helpers_tests") {
			load { displayTests() }
		}.generate()
	}

	test("display entity selectors") {
		dataPack("helpers_tests") {
			load { displayEntitySelectorTests() }
		}.generate()
	}

	test("display to entity") {
		dataPack("helpers_tests") {
			load { displayToEntityTests() }
		}.generate()
	}
})
