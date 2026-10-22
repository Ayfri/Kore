package io.github.ayfri.kore.helpers

import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.ARGB
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.colors.RGB
import io.github.ayfri.kore.arguments.components.item.*
import io.github.ayfri.kore.arguments.maths.Vec3f
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.numbers.seconds
import io.github.ayfri.kore.arguments.types.literals.UUIDArgument
import io.github.ayfri.kore.arguments.types.literals.allEntities
import io.github.ayfri.kore.assertions.assertsIs
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
import io.github.ayfri.kore.generated.Attributes
import io.github.ayfri.kore.generated.Blocks
import io.github.ayfri.kore.generated.Enchantments
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.helpers.displays.*
import io.github.ayfri.kore.helpers.displays.entities.BillboardMode
import io.github.ayfri.kore.helpers.displays.entities.Brightness
import io.github.ayfri.kore.helpers.displays.entities.ItemDisplayModelMode
import io.github.ayfri.kore.helpers.displays.maths.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
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

fun Function.displayNbtTests() {
	val textDisplay = textDisplay {
		text("hi")
		background = ARGB(128, 255, 0, 0)
		billboardMode = BillboardMode.CENTER
		brightness = Brightness(block = 15, sky = 10)
		glowColorOverride = RGB(255, 0, 0)
	}

	summon(textDisplay.entityType, vec3(), textDisplay.toNbt()) assertsIs """summon minecraft:text_display ~ ~ ~ {billboard:"center",brightness:{block:15,sky:10},glow_color_override:16711680,background:-2130771968,text:"hi"}"""

	val richTextDisplay = textDisplay {
		text = textComponent("Legendary ", Color.GOLD) + textComponent("Sword")
	}

	summon(richTextDisplay.entityType, vec3(), richTextDisplay.toNbt()) assertsIs """summon minecraft:text_display ~ ~ ~ {text:[{type:"text",text:""},{type:"text",color:"gold",text:"Legendary "},{type:"text",text:"Sword"}]}"""

	val sizedDisplay = blockDisplay {
		blockState(Blocks.STONE)
		height = 0.5f
		teleportDuration = 3
		viewRange = 0.5f
		width = 1.5f
	}

	summon(sizedDisplay.entityType, vec3(), sizedDisplay.toNbt()) assertsIs """summon minecraft:block_display ~ ~ ~ {height:0.5f,teleport_duration:3,view_range:0.5f,width:1.5f,block_state:"minecraft:stone"}"""

	val stoneDisplay = blockDisplay { blockState(Blocks.STONE) }
	summon(stoneDisplay.entityType, vec3(), stoneDisplay.toNbt()) assertsIs """summon minecraft:block_display ~ ~ ~ {block_state:"minecraft:stone"}"""

	val snowyDisplay = blockDisplay {
		blockState(Blocks.GRASS_BLOCK) {
			properties {
				this["snowy"] = true
			}
		}
	}
	summon(snowyDisplay.entityType, vec3(), snowyDisplay.toNbt()) assertsIs """summon minecraft:block_display ~ ~ ~ {block_state:{id:"minecraft:grass_block",properties:{snowy:"true"}}}"""
}

fun Function.displayTransformationTests() {
	fun nbtOf(block: Transformation.() -> Unit) =
		blockDisplay { blockState(Blocks.STONE); transformation(block) }.toNbt().toString()

	nbtOf {
		scale = Vec3f(2, 2, 2)
		translation { y = 2f }
	} assertsIs """{transformation:{left_rotation:[0.0f,0.0f,0.0f,1.0f],right_rotation:[0.0f,0.0f,0.0f,1.0f],scale:[2.0f,2.0f,2.0f],translation:[0.0f,2.0f,0.0f]},block_state:"minecraft:stone"}"""

	nbtOf {
		scale = Vec3f(2, 2, 2)
	} assertsIs """{transformation:{left_rotation:[0.0f,0.0f,0.0f,1.0f],right_rotation:[0.0f,0.0f,0.0f,1.0f],scale:[2.0f,2.0f,2.0f],translation:[0.0f,0.0f,0.0f]},block_state:"minecraft:stone"}"""

	nbtOf {
		leftRotation { quaternionNormalized(0, 0.6, 0, 0.8) }
	} assertsIs """{transformation:{left_rotation:[0.0f,0.6f,0.0f,0.8f],right_rotation:[0.0f,0.0f,0.0f,1.0f],scale:[1.0f,1.0f,1.0f],translation:[0.0f,0.0f,0.0f]},block_state:"minecraft:stone"}"""

	nbtOf {
		matrix {
			row(2f, 0f, 0f, 0f)
			row(0f, 2f, 0f, 2f)
			row(0f, 0f, 2f, 0f)
			row(0f, 0f, 0f, 1f)
		}
	} assertsIs """{transformation:[2.0f,0.0f,0.0f,0.0f,0.0f,2.0f,0.0f,2.0f,0.0f,0.0f,2.0f,0.0f,0.0f,0.0f,0.0f,1.0f],block_state:"minecraft:stone"}"""

	val display = blockDisplay {
		blockState(Blocks.STONE)
		transformation { scale = Vec3f(2, 2, 2) }
	}
	val first = display.toNbt()

	display.transformation!!.matrix shouldBe null
	display.toNbt() shouldBe first

	display.interpolable(vec3()).interpolateTo(20) { scale = Vec3f(2f, 2f, 2f) }
	lines.last().replace(".0f", "f") shouldMatch Regex("""data merge entity \S+ \{interpolation_duration:20,start_interpolation:0,transformation:\{left_rotation:\[0f,0f,0f,1f\],right_rotation:\[0f,0f,0f,1f\],scale:\[2f,2f,2f\],translation:\[0f,0f,0f\]\}\}""")
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
		}
	}

	test("display nbt") {
		dataPack("helpers_tests") {
			load { displayNbtTests() }
		}
	}

	test("display transformation") {
		dataPack("helpers_tests") {
			load { displayTransformationTests() }
		}
	}

	test("display entity selectors") {
		dataPack("helpers_tests") {
			load { displayEntitySelectorTests() }
		}
	}

	test("display to entity") {
		dataPack("helpers_tests") {
			load { displayToEntityTests() }
		}
	}

	test("interpolable UUIDs are unique per display and identical across builds") {
		fun build(): List<String> {
			lateinit var lines: List<String>
			dataPack("helpers_tests") {
				load {
					repeat(2) { blockDisplay { blockState(Blocks.STONE) }.interpolable(vec3(0, 0, 0)).summon() }
					itemDisplay { item(Items.DIAMOND) }.interpolable(vec3(0, 0, 0)).summon()
					lines = this.lines
				}
			}
			return lines
		}

		val lines = build()
		lines.distinct() shouldHaveSize 3
		lines shouldBe build()
	}
})
