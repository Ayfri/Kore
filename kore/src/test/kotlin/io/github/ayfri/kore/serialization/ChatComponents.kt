package io.github.ayfri.kore.serialization

import io.github.ayfri.kore.arguments.chatcomponents.*
import io.github.ayfri.kore.arguments.chatcomponents.events.showItem
import io.github.ayfri.kore.arguments.chatcomponents.events.showText
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.components.types.damage
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.types.literals.randomPlayer
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.arguments.types.resources.storage
import io.github.ayfri.kore.assertions.assertsIsJson
import io.github.ayfri.kore.generated.Items

fun chatComponentsTests() {
	val simplePlainText = textComponent("Hello, world!")
	simplePlainText assertsIsJson "\"Hello, world!\""

	val combinedPlainText = textComponent("Hello, ") + text("world!") {
		color = Color.RED
		bold = true
	}

	combinedPlainText assertsIsJson """
		[
			{
				"type": "text",
				"text": "Hello, "
			},
			{
				"type": "text",
				"text": "world!",
				"color": "red",
				"bold": true
			}
		]
	""".trimIndent()

	val entityComponent = entityComponent(self(), separator = " ")
	entityComponent assertsIsJson """
		{
			"type": "selector",
			"selector": "@s",
			"separator": " "
		}
	""".trimIndent()

	val keybindComponent = keybindComponent("key.sprint")
	keybindComponent assertsIsJson """
		{
			"type": "keybind",
			"keybind": "key.sprint"
		}
	""".trimIndent()

	val nbtComponentEntity = nbtComponent("test", self())
	nbtComponentEntity assertsIsJson """
		{
			"type": "nbt",
			"nbt": "test",
			"entity": "@s",
			"source": "entity"
		}
	""".trimIndent()

	val nbtComponentBlock = nbtComponent("test", vec3(0, 0, 0))
	nbtComponentBlock assertsIsJson """
		{
			"type": "nbt",
			"nbt": "test",
			"block": "0 0 0",
			"source": "block"
		}
	""".trimIndent()

	val nbtComponentStorage = nbtComponent("test", storage("my_storage"))
	nbtComponentStorage assertsIsJson """
		{
			"type": "nbt",
			"nbt": "test",
			"storage": "minecraft:my_storage",
			"source": "storage"
		}
	""".trimIndent()

	val scoreComponent = scoreComponent("my_objective", randomPlayer())
	scoreComponent assertsIsJson """
		{
			"type": "score",
			"score": {
				"name": "@r",
				"objective": "my_objective"
			}
		}
	""".trimIndent()

	val scoreSelfComponent = scoreComponent("my_objective")
	scoreSelfComponent assertsIsJson """
		{
			"type": "score",
			"score": {
				"name": "*",
				"objective": "my_objective"
			}
		}
	""".trimIndent()

	val translatedComponent = translatedTextComponent("block.minecraft.stone")
	translatedComponent assertsIsJson """
		{
			"type": "translatable",
			"translate": "block.minecraft.stone"
		}
	""".trimIndent()

	val translatedComponentWithArgs = translatedTextComponent("chat.type.advancement.goal", listOf("Kore", "The best library!"))
	translatedComponentWithArgs assertsIsJson """
		{
			"type": "translatable",
			"translate": "chat.type.advancement.goal",
			"with": [
				{
					"type": "text",
					"text": "Kore"
				},
				{
					"type": "text",
					"text": "The best library!"
				}
			]
		}
	""".trimIndent()

	val hoverEventComponent = textComponent("Hover me!") {
		hoverEvent {
			showText("Hello, world!")
		}
	}

	hoverEventComponent assertsIsJson """
		{
			"type": "text",
			"text": "Hover me!",
			"hoverEvent": {
				"action": "show_text",
				"value": "Hello, world!"
			}
		}
	""".trimIndent()

	val hoverEventComponentWithItem = textComponent("Hover me!") {
		hoverEvent {
			showItem(Items.DIAMOND_SWORD {
				damage(5)
			}, 5)
		}
	}

	hoverEventComponentWithItem assertsIsJson """
		{
			"type": "text",
			"text": "Hover me!",
			"hoverEvent": {
				"action": "show_item",
				"value": "",
				"contents": {
					"id": "minecraft:diamond_sword",
					"count": 5,
					"components": {
						"damage": 5
					}
				}
			}
		}
	""".trimIndent()
}
