package io.github.ayfri.kore.helpers

import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.types.literals.rotation
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.say
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.entities.kill
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.helpers.mannequins.MannequinPose
import io.github.ayfri.kore.helpers.npcs.npc
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch

class NpcTests : FunSpec({
	test("summon places the mannequin then the interaction at the same position") {
		dataPack("npc_tests") {
			val bob = npc("bob") { }

			function("setup") { bob.summon(vec3(0, 64, 0)) }

			val lines = functions.single().lines
			lines shouldHaveSize 2
			lines[0] shouldMatch Regex(
				"""summon minecraft:mannequin 0 64 0 \{Invulnerable:1b,Tags:\["npc_tests.npc.bob"\],immovable:1b,UUID:\[I;.*?\]\}"""
			)
			lines[1] shouldMatch Regex(
				"""summon minecraft:interaction 0 64 0 \{Tags:\["npc_tests.interaction.npc_bob"\],height:1.9f,width:0.7f,UUID:\[I;.*?\]\}"""
			)
		}
	}

	test("the interaction is 0.1 larger than the pose hitbox") {
		val sizes = mapOf(
			MannequinPose.STANDING to "height:1.9f,width:0.7f",
			MannequinPose.CROUCHING to "height:1.6f,width:0.7f",
			MannequinPose.SWIMMING to "height:0.7f,width:0.7f",
			MannequinPose.FALL_FLYING to "height:0.7f,width:0.7f",
			MannequinPose.SLEEPING to "height:0.3f,width:0.3f",
		)

		sizes.forEach { (pose, size) ->
			dataPack("npc_tests") {
				val bob = npc("bob") { mannequin { this.pose = pose } }

				bob.interaction.toNbt().toString() shouldMatch Regex(""".*$size\}""")
			}
		}
	}

	test("a size set on the interaction wins over the pose") {
		dataPack("npc_tests") {
			val bob = npc("bob") {
				mannequin { pose = MannequinPose.CROUCHING }
				interaction { width = 2f }
			}

			bob.interaction.toNbt().toString() assertsIs "{height:1.6f,width:2.0f}"
		}
	}

	test("the NPC tag is added after the tags of the mannequin") {
		dataPack("npc_tests") {
			val bob = npc("bob") { mannequin { tags = listOf("shop") } }

			bob.mannequin.toNbt().toString() assertsIs """{Invulnerable:1b,Tags:["shop","npc_tests.npc.bob"],immovable:1b}"""
		}
	}

	test("objectives are created on load once each, including the ones declared in a callback") {
		dataPack("npc_tests") {
			npc("bob") {
				objective("visits") assertsIs "npc_tests.npc.bob.visits"
				objective("visits")
				onRightClick { say(objective("seen")) }
			}

			generatedFunctions.first { it.name == "npc_bob_load" }.lines shouldBe listOf(
				"scoreboard objectives add npc_tests.npc.bob.visits dummy",
				"scoreboard objectives add npc_tests.npc.bob.seen dummy",
			)
		}
	}

	test("an NPC without objectives generates no load function") {
		dataPack("npc_tests") {
			npc("bob") { onRightClick { say("Hi") } }

			generatedFunctions.none { it.name == "npc_bob_load" } shouldBe true
		}
	}

	test("callbacks receive the mannequin as clicked") {
		dataPack("npc_tests") {
			npc("bob") {
				onLeftClick { clicked -> clicked.kill() }
				onRightClick { clicked -> clicked.kill() }
			}

			val selector = "@e[distance=..0.5,limit=1,sort=nearest,tag=npc_tests.npc.bob,type=minecraft:mannequin]"
			generatedFunctions.first { it.name == "interaction_npc_bob_left_click_hit" }.lines[1] assertsIs
				"execute at @s on attacker run kill $selector"
			generatedFunctions.first { it.name == "interaction_npc_bob_right_click_hit" }.lines[1] assertsIs
				"execute at @s on target run kill $selector"
		}
	}

	test("click advancements and functions are generated for the interaction of the NPC") {
		dataPack("npc_tests") {
			npc("bob") {
				onLeftClick { say("Hit") }
				onRightClick { say("Hello") }
			}

			advancements.map { it.fileName } shouldBe listOf("interaction/npc_bob_left_click", "interaction/npc_bob_right_click")
			generatedFunctions.map { it.name } shouldBe listOf(
				"interaction_left_click_target",
				"interaction_npc_bob_left_click_hit",
				"interaction_npc_bob_left_click",
				"interaction_right_click_target",
				"interaction_npc_bob_right_click_hit",
				"interaction_npc_bob_right_click",
			)
		}
	}

	test("the handle kills and teleports both entities") {
		dataPack("npc_tests") {
			val bob = npc("bob") { }

			function("setup") {
				val summoned = bob.summon(vec3(0, 64, 0))
				summoned.kill()
				summoned.teleportTo(vec3(0, 64, 5), rotation(90, 0))
			}

			val lines = functions.single().lines.drop(2)
			lines[0] shouldMatch Regex("""kill @e\[limit=1,nbt=\{UUID:\[I;.*?\]\},type=minecraft:mannequin\]""")
			lines[1] shouldMatch Regex("""kill @e\[limit=1,nbt=\{UUID:\[I;.*?\]\},type=minecraft:interaction\]""")
			lines[2] shouldMatch Regex("""teleport @e\[limit=1,nbt=\{UUID:\[I;.*?\]\},type=minecraft:mannequin\] 0 64 5 90 0""")
			lines[3] shouldMatch Regex("""teleport @e\[limit=1,nbt=\{UUID:\[I;.*?\]\},type=minecraft:interaction\] 0 64 5""")
			lines shouldHaveSize 4
		}
	}

	test("names are unique per pack") {
		dataPack("npc_tests") {
			npc("bob") { }

			shouldThrow<IllegalArgumentException> { npc("bob") { } }.message shouldBe
				"Interaction 'npc_bob' is already declared in this pack."
			shouldThrow<IllegalArgumentException> { npc("Bad Name") { } }
		}
	}

	test("builds are reproducible") {
		fun build() = dataPack("npc_tests") {
			val bob = npc("bob") {
				objective("visits")
				onRightClick { say("Hello") }
			}
			function("setup") { bob.summon(vec3(0, 64, 0)) }
		}.let {
			it.functions.single().lines to (it.generatedFunctions.map { f -> "${f.asId()}\n$f" } + it.advancements.map { a -> a.fileName })
		}

		build() shouldBe build()
	}
})
