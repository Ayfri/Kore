package io.github.ayfri.kore.helpers

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.types.literals.UUIDArgument
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.entities.InteractionEntity
import io.github.ayfri.kore.entities.kill
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.functions.generatedFunctionName
import io.github.ayfri.kore.helpers.interactions.interaction
import io.github.ayfri.kore.helpers.interactions.onLeftClick
import io.github.ayfri.kore.helpers.interactions.onRightClick
import io.github.ayfri.kore.helpers.interactions.summon
import io.github.ayfri.kore.utils.pretty
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch
import kotlin.uuid.Uuid

private fun DataPack.advancementsJson() = advancements.joinToString("\n") { "# ${it.fileName}\n${it.generateJson(this)}" }

private fun DataPack.functionsText() = generatedFunctions.joinToString("\n\n") { "# ${it.name}\n$it" }

class InteractionTests : FunSpec({
	test("model nbt") {
		interaction {
			height = 1.9f
			response = true
			width = 0.7f
		}.toNbt().toString() assertsIs "{height:1.9f,response:1b,width:0.7f}"
	}

	test("right click generates one advancement and three functions") {
		dataPack("interaction_tests") {
			pretty()
			interaction("lever") {
				height = 0.5f
				width = 0.5f
				onRightClick { say("Pulled") }
			}

			advancementsJson() assertsIs """
				# interaction/lever_right_click
				{
					"criteria": {
						"right_click": {
							"trigger": "minecraft:player_interacted_with_entity",
							"conditions": {
								"entity": {
									"type": "minecraft:entity_properties",
									"entity": "this",
									"predicate": {
										"minecraft:entity_type": "minecraft:interaction",
										"minecraft:entity_tags": {
											"all_of": [
												"interaction_tests.interaction.lever"
											]
										}
									}
								}
							}
						}
					},
					"rewards": {
						"function": "interaction_tests:generated_scopes/interaction_lever_right_click"
					}
				}
			""".trimIndent()

			functionsText() assertsIs """
				# interaction_right_click_target
				execute on target if entity @s[tag=interaction_tests.interaction.source] run return 1
				return fail

				# interaction_lever_right_click_hit
				tag @s add interaction_tests.interaction.clicked
				execute at @s on target run say Pulled
				data remove entity @s interaction
				tag @s remove interaction_tests.interaction.clicked

				# interaction_lever_right_click
				advancement revoke @s only interaction_tests:interaction/lever_right_click
				tag @s add interaction_tests.interaction.source
				execute as @e[distance=..24,tag=interaction_tests.interaction.lever,type=minecraft:interaction] if function interaction_tests:generated_scopes/interaction_right_click_target run function interaction_tests:generated_scopes/interaction_lever_right_click_hit
				tag @s remove interaction_tests.interaction.source
			""".trimIndent()
		}
	}

	test("left click listens to the attacker") {
		dataPack("interaction_tests") {
			pretty()
			interaction("button") {
				onLeftClick { say("Hit") }
			}

			advancementsJson() assertsIs """
				# interaction/button_left_click
				{
					"criteria": {
						"left_click": {
							"trigger": "minecraft:player_hurt_entity",
							"conditions": {
								"entity": {
									"type": "minecraft:entity_properties",
									"entity": "this",
									"predicate": {
										"minecraft:entity_type": "minecraft:interaction",
										"minecraft:entity_tags": {
											"all_of": [
												"interaction_tests.interaction.button"
											]
										}
									}
								}
							}
						}
					},
					"rewards": {
						"function": "interaction_tests:generated_scopes/interaction_button_left_click"
					}
				}
			""".trimIndent()

			functionsText() assertsIs """
				# interaction_left_click_target
				execute on attacker if entity @s[tag=interaction_tests.interaction.source] run return 1
				return fail

				# interaction_button_left_click_hit
				tag @s add interaction_tests.interaction.clicked
				execute at @s on attacker run say Hit
				data remove entity @s attack
				tag @s remove interaction_tests.interaction.clicked

				# interaction_button_left_click
				advancement revoke @s only interaction_tests:interaction/button_left_click
				tag @s add interaction_tests.interaction.source
				execute as @e[distance=..24,tag=interaction_tests.interaction.button,type=minecraft:interaction] if function interaction_tests:generated_scopes/interaction_left_click_target run function interaction_tests:generated_scopes/interaction_button_left_click_hit
				tag @s remove interaction_tests.interaction.source
			""".trimIndent()
		}
	}

	test("several callbacks run in declaration order inside one generated function") {
		dataPack("interaction_tests") {
			interaction("door") {
				onRightClick { say("One") }
				onRightClick { say("Two") }
			}

			val body = generatedFunctions.first { it.name == "interaction_door_right_click_hit" }.lines
			val callbacks = listOf("say One", "say Two")
			val name = generatedFunctionName("generated", listOf("say One", "say Two"))

			body.drop(1).dropLast(2) shouldBe listOf("execute at @s on target run function interaction_tests:generated_scopes/$name")
			generatedFunctions.first { it.name == name }.lines shouldBe callbacks
		}
	}

	test("a callback can select the clicked interaction") {
		dataPack("interaction_tests") {
			interaction("crate") {
				onLeftClick { clicked -> clicked.kill() }
			}

			generatedFunctions.first { it.name == "interaction_crate_left_click_hit" }.lines[1] assertsIs
				"execute at @s on attacker run kill @e[limit=1,tag=interaction_tests.interaction.clicked,type=minecraft:interaction]"
		}
	}

	test("two definitions share the target function") {
		dataPack("interaction_tests") {
			interaction("first") { onRightClick { say("1") } }
			interaction("second") { onRightClick { say("2") } }

			generatedFunctions.map { it.name } shouldBe listOf(
				"interaction_right_click_target",
				"interaction_first_right_click_hit",
				"interaction_first_right_click",
				"interaction_second_right_click_hit",
				"interaction_second_right_click",
			)
			advancements.map { it.fileName } shouldBe listOf("interaction/first_right_click", "interaction/second_right_click")
		}
	}

	test("the search radius is set per interaction and stays out of the NBT") {
		dataPack("interaction_tests") {
			val wide = interaction("wide") {
				searchRadius = 40
				onRightClick { say("Wide") }
			}
			interaction("narrow") { onLeftClick { say("Narrow") } }

			generatedFunctions.first { it.name == "interaction_wide_right_click" }.lines[2] assertsIs
				"execute as @e[distance=..40,tag=interaction_tests.interaction.wide,type=minecraft:interaction] if function interaction_tests:generated_scopes/interaction_right_click_target run function interaction_tests:generated_scopes/interaction_wide_right_click_hit"
			generatedFunctions.first { it.name == "interaction_narrow_left_click" }.lines[2] assertsIs
				"execute as @e[distance=..24,tag=interaction_tests.interaction.narrow,type=minecraft:interaction] if function interaction_tests:generated_scopes/interaction_left_click_target run function interaction_tests:generated_scopes/interaction_narrow_left_click_hit"
			wide.toNbt().toString() assertsIs "{}"
		}
	}

	test("an interaction without callbacks generates nothing but is still tagged") {
		dataPack("interaction_tests") {
			interaction("decor") { width = 2f }

			advancements shouldHaveSize 0
			generatedFunctions shouldHaveSize 0

			function("setup") {
				interaction("ignored") { }.summon(vec3(0, 64, 0))
			}
			functions.single().lines.single() shouldMatch Regex(
				"""summon minecraft:interaction 0 64 0 \{Tags:\["interaction_tests.interaction.ignored"\],UUID:\[I;.*?\]\}"""
			)
		}
	}

	test("summon tags the instance with its definition") {
		dataPack("interaction_tests") {
			val lever = interaction("lever") {
				height = 0.5f
				width = 0.5f
				tags = listOf("custom")
				onRightClick { say("Pulled") }
			}

			function("setup") {
				lever.summon(vec3(0, 64, 0))
			}

			functions.single().lines.single() shouldMatch Regex(
				"""summon minecraft:interaction 0 64 0 \{height:0.5f,Tags:\["custom","interaction_tests.interaction.lever"\],width:0.5f,UUID:\[I;.*?\]\}"""
			)
		}
	}

	test("an undeclared interaction with callbacks refuses to summon") {
		dataPack("interaction_tests") {
			val raw = interaction { onRightClick { say("never") } }

			function("setup") {
				shouldThrow<IllegalStateException> { raw.summon(vec3(0, 64, 0)) }.message shouldBe
					"Interaction has click callbacks but wasn't declared with dataPack.interaction(name) { }, so they were never generated."
			}
		}
	}

	test("names are validated and unique per pack") {
		dataPack("interaction_tests") {
			interaction("lever") { }

			shouldThrow<IllegalArgumentException> { interaction("lever") { } }.message shouldBe
				"Interaction 'lever' is already declared in this pack."
			shouldThrow<IllegalArgumentException> { interaction("Bad Name") { } }
		}
	}

	test("builds are reproducible") {
		fun build() = dataPack("interaction_tests") {
			val lever = interaction("lever") { onLeftClick { say("Hit") }; onRightClick { say("Pulled") } }
			function("setup") { lever.summon(vec3(0, 64, 0)) }
		}.let { it.functions.single().lines to (it.generatedFunctions.map { f -> f.asId() } + it.advancements.map { a -> a.fileName }) }

		build() shouldBe build()
	}

	test("entity handle targets the interaction by uuid") {
		dataPack("interaction_tests") {
			function("setup") {
				InteractionEntity(UUIDArgument(Uuid.parse("00000000-0000-0000-0000-000000000001"))).kill() assertsIs
					"kill @e[limit=1,nbt={UUID:[I;0,0,0,1]},type=minecraft:interaction]"
			}
		}
	}
})
