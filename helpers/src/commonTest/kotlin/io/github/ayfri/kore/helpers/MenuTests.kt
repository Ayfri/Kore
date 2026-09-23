package io.github.ayfri.kore.helpers

import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.helpers.menus.menu
import io.github.ayfri.kore.utils.pretty
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec

class MenuTests : FunSpec({
	test("pages, buttons and trigger dispatch") {
		dataPack("menu_tests") {
			pretty()
			val settings = menu("settings", "My Pack", Color.GOLD) {
				text("Tweak the pack.")
				button("Heal me", tooltip = "Instant health") { say("healed") }
				button("Count", reopen = true) { say("counted") }
				page("Credits") {
					text("Made by Ayfri")
					link("GitHub", "https://github.com/Ayfri/Kore")
				}
				pauseScreen = true
			}

			function("open") { settings.open() }.toString() assertsIs "dialog show @s menu_tests:settings"

			dialogs.joinToString("\n") { it.generateJson(this) } assertsIs """
				{
					"type": "minecraft:multi_action",
					"title": "Credits",
					"body": {
						"type": "minecraft:plain_message",
						"contents": "Made by Ayfri"
					},
					"inputs": [],
					"actions": [
						{
							"action": {
								"type": "minecraft:open_url",
								"url": "https://github.com/Ayfri/Kore"
							},
							"label": "GitHub"
						}
					],
					"exit_action": {
						"action": {
							"type": "minecraft:show_dialog",
							"dialog": "menu_tests:settings"
						},
						"label": {
							"translate": "gui.back",
							"type": "translatable"
						},
						"width": 200
					}
				}
				{
					"type": "minecraft:multi_action",
					"title": {
						"text": "My Pack",
						"color": "gold",
						"type": "text"
					},
					"body": {
						"type": "minecraft:plain_message",
						"contents": "Tweak the pack."
					},
					"inputs": [],
					"actions": [
						{
							"action": {
								"type": "minecraft:run_command",
								"command": "trigger menu_tests.menu.settings set 1"
							},
							"label": "Heal me",
							"tooltip": "Instant health"
						},
						{
							"action": {
								"type": "minecraft:run_command",
								"command": "trigger menu_tests.menu.settings set 2"
							},
							"label": "Count"
						},
						{
							"action": {
								"type": "minecraft:show_dialog",
								"dialog": "menu_tests:settings_credits"
							},
							"label": "Credits"
						}
					],
					"exit_action": {
						"label": {
							"translate": "gui.done",
							"type": "translatable"
						},
						"width": 200
					}
				}
			""".trimIndent()

			generatedFunctions.joinToString("\n\n") { "# ${it.asId()}\n$it" } assertsIs """
				# menu_tests:generated_scopes/menu_settings_1
				say healed

				# menu_tests:generated_scopes/menu_settings_2
				say counted
				dialog show @s menu_tests:settings

				# menu_tests:generated_scopes/menu_settings_dispatch
				execute if score @s menu_tests.menu.settings matches 1 run function menu_tests:generated_scopes/menu_settings_1
				execute if score @s menu_tests.menu.settings matches 2 run function menu_tests:generated_scopes/menu_settings_2
				scoreboard players reset @s menu_tests.menu.settings

				# menu_tests:generated_scopes/menu_settings_load
				scoreboard objectives add menu_tests.menu.settings trigger

				# menu_tests:generated_scopes/menu_settings_tick
				scoreboard players enable @a menu_tests.menu.settings
				execute as @a[scores={menu_tests.menu.settings=1..}] at @s run function menu_tests:generated_scopes/menu_settings_dispatch
			""".trimIndent()

			tags.first { it.type == "dialog" }.generateJson(this) assertsIs """
				{
					"replace": false,
					"values": [
						{
							"id": "menu_tests:settings",
							"required": false
						}
					]
				}
			""".trimIndent()
		}
	}

	test("smithed data pack menu") {
		dataPack("menu_tests") {
			pretty()
			menu("about", "Example Data Pack") {
				link("Website", "https://example.com/")
				smithed = true
			}

			dialogs.joinToString("\n") { "${it.namespace ?: name}:${it.fileName} ${it.generateJson(this)}" } assertsIs """
				menu_tests:about {
					"type": "minecraft:multi_action",
					"title": "Example Data Pack",
					"inputs": [],
					"actions": [
						{
							"action": {
								"type": "minecraft:open_url",
								"url": "https://example.com/"
							},
							"label": "Website"
						}
					],
					"exit_action": {
						"action": {
							"type": "minecraft:show_dialog",
							"dialog": "smithed:data_packs"
						},
						"label": {
							"translate": "gui.back",
							"type": "translatable"
						},
						"width": 200
					}
				}
				smithed:data_packs {
					"type": "minecraft:dialog_list",
					"title": {
						"translate": "menu.smithed.data_packs.title",
						"with": [
							{
								"translate": "selectWorld.dataPacks",
								"type": "translatable"
							}
						],
						"fallback": "%s",
						"type": "translatable"
					},
					"external_title": {
						"translate": "menu.smithed.data_packs",
						"with": [
							{
								"translate": "selectWorld.dataPacks",
								"type": "translatable"
							}
						],
						"fallback": "%s...",
						"type": "translatable"
					},
					"inputs": [],
					"dialogs": "#smithed:data_packs",
					"exit_action": {
						"label": {
							"translate": "gui.back",
							"type": "translatable"
						},
						"width": 200
					}
				}
			""".trimIndent()

			tags.joinToString("\n") { "${it.namespace}/${it.type}/${it.fileName}: ${it.generateJson(this)}" } assertsIs """
				smithed/dialog/data_packs: {
					"replace": false,
					"values": [
						{
							"id": "menu_tests:about",
							"required": false
						}
					]
				}
				minecraft/dialog/pause_screen_additions: {
					"replace": false,
					"values": [
						{
							"id": "smithed:data_packs",
							"required": false
						}
					]
				}
			""".trimIndent()
		}
	}

	test("a page without buttons is rejected") {
		shouldThrow<IllegalArgumentException> {
			dataPack("menu_tests") { menu("empty", "Empty") { text("Nothing here.") } }
		}
	}
})
