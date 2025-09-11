package io.github.ayfri.kore.serialization

import io.github.ayfri.kore.arguments.actions.*
import io.github.ayfri.kore.arguments.chatcomponents.*
import io.github.ayfri.kore.arguments.chatcomponents.hover.showItem
import io.github.ayfri.kore.arguments.chatcomponents.hover.showText
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.components.item.damage
import io.github.ayfri.kore.arguments.components.item.lore
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.types.literals.randomPlayer
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.arguments.types.resources.storage
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.assertions.assertsIsJson
import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.predicates.conditions.matchTool
import io.github.ayfri.kore.features.predicates.predicate
import io.github.ayfri.kore.features.predicates.sub.components
import io.github.ayfri.kore.generated.Dialogs
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.utils.pretty
import io.github.ayfri.kore.utils.set

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
				"bold": true,
				"color": "red",
				"text": "world!"
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
			"entity": "@s",
			"nbt": "test",
			"source": "entity"
		}
	""".trimIndent()

	val nbtComponentBlock = nbtComponent("test", vec3(0, 0, 0))
	nbtComponentBlock assertsIsJson """
		{
			"type": "nbt",
			"block": "0 0 0",
			"nbt": "test",
			"source": "block"
		}
	""".trimIndent()

	val nbtComponentStorage = nbtComponent("test", storage("my_storage"))
	nbtComponentStorage assertsIsJson """
		{
			"type": "nbt",
			"nbt": "test",
			"source": "storage",
			"storage": "minecraft:my_storage"
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
			"hover_event": {
				"action": "show_text",
				"value": "Hello, world!"
			},
			"text": "Hover me!"
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
			"hover_event": {
				"action": "show_item",
				"id": "minecraft:diamond_sword",
				"count": 5,
				"components": {
					"damage": 5
				}
			},
			"text": "Hover me!"
		}
	""".trimIndent()


	dataPack("components_serialization") {
		pretty()

		predicate("escaped_list") {
			matchTool {
				components {
					lore("test", Color.RED)
				}
			}
		}

		predicates.last() assertsIs """
			{
				"condition": "minecraft:match_tool",
				"predicate": {
					"components": {
						"lore": "[{type:\"text\",color:\"red\",text:\"test\"}]"
					}
				}
			}
		""".trimIndent()

		predicate("escaped_list2") {
			matchTool {
				components {
					lore(textComponent("text1") + text("text2") + text("text3"))
				}
			}
		}

		predicates.last() assertsIs """
			{
				"condition": "minecraft:match_tool",
				"predicate": {
					"components": {
						"lore": "[{type:\"text\",text:\"text1\"},{type:\"text\",text:\"text2\"},{type:\"text\",text:\"text3\"}]"
					}
				}
			}
		""".trimIndent()
	}

	chatComponentsOnClick()
	chatComponentAllFields()
}

private fun chatComponentsOnClick() {
	textComponent("change page") {
		clickEvent {
			changePage(2)
		}
	} assertsIsJson """
		{
			"type": "text",
			"click_event": {
				"action": "change_page",
				"page": 2
			},
			"text": "change page"
		}
	""".trimIndent()

	textComponent("copy to clipboard") {
		clickEvent {
			copyToClipboard("test")
		}
	} assertsIsJson """
		{
			"type": "text",
			"click_event": {
				"action": "copy_to_clipboard",
				"value": "test"
			},
			"text": "copy to clipboard"
		}
	""".trimIndent()

	textComponent("custom") {
		clickEvent {
			custom("test") {
				this["data"] = "thing"
			}
		}
	} assertsIsJson """
		{
			"type": "text",
			"click_event": {
				"action": "custom",
				"id": "test",
				"payload": {
					"data": "thing"
				}
			},
			"text": "custom"
		}
	""".trimIndent()

	textComponent("open url") {
		clickEvent {
			openUrl("https://www.google.com")
		}
	} assertsIsJson """
		{
			"type": "text",
			"click_event": {
				"action": "open_url",
				"url": "https://www.google.com"
			},
			"text": "open url"
		}
	""".trimIndent()

	textComponent("show dialog") {
		clickEvent {
			showDialog(Dialogs.CUSTOM_OPTIONS)
		}
	} assertsIsJson """
		{
			"type": "text",
			"click_event": {
				"action": "show_dialog",
				"dialog": "minecraft:custom_options"
			},
			"text": "show dialog"
		}
	""".trimIndent()

	textComponent("suggest command") {
		clickEvent {
			suggestCommand {
				say("Suggest command")
			}
		}
	} assertsIsJson """
		{
			"type": "text",
			"click_event": {
				"action": "suggest_command",
				"command": "/say Suggest command"
			},
			"text": "suggest command"
		}
	""".trimIndent()

	textComponent("run command") {
		clickEvent {
			runCommand {
				say("Run command")
			}
		}
	} assertsIsJson """
		{
			"type": "text",
			"click_event": {
				"action": "run_command",
				"command": "say Run command"
			},
			"text": "run command"
		}
	""".trimIndent()
}

private fun chatComponentAllFields() {
	val component = textComponent("Hello, world!") {
		bold = true
		italic = true
		underlined = true
		strikethrough = true
		obfuscated = true
		color = Color.RED
		shadowColor = Color.BLUE
		insertion = "insertion"
		clickEvent {
			runCommand {
				say("Run command")
			}
		}
		hoverEvent {
			showText("show_text")
		}
	}

	component assertsIsJson """
		{
			"type": "text",
			"bold": true,
			"click_event": {
				"action": "run_command",
				"command": "say Run command"
			},
			"color": "red",
			"hover_event": {
				"action": "show_text",
				"value": "show_text"
			},
			"insertion": "insertion",
			"italic": true,
			"obfuscated": true,
			"shadow_color": "blue",
			"strikethrough": true,
			"text": "Hello, world!",
			"underlined": true
		}
	""".trimIndent()
}
