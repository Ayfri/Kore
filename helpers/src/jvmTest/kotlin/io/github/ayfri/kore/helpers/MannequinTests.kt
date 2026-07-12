package io.github.ayfri.kore.helpers

import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.components.item.MannequinModel
import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.types.literals.UUIDArgument
import io.github.ayfri.kore.commands.SwingHand
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.entities.MannequinEntity
import io.github.ayfri.kore.entities.kill
import io.github.ayfri.kore.entities.swing
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.helpers.mannequins.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldMatch
import kotlin.uuid.Uuid

fun Function.mannequinTests() {
	val m = mannequin {
		hiddenLayers(MannequinLayer.CAPE, MannequinLayer.HAT)
		mainHand = MannequinHand.LEFT
		playerProfile("Ayfri")
	}

	m.toNbt().toString() assertsIs "{hidden_layers:[\"cape\",\"hat\"],main_hand:\"left\",profile:{name:\"Ayfri\"}}"

	val m2 = mannequin {
		textureProfile("tex") {
			model = MannequinModel.SLIM
		}
	}

	m2.toNbt().toString() assertsIs "{profile:{model:\"slim\",texture:\"minecraft:tex\"}}"

	val m3 = mannequin {
		description = textComponent("Test")
		hideDescription = false
		immovable = true
		pose = MannequinPose.CROUCHING
	}

	m3.toNbt().toString() assertsIs "{description:\"Test\",hide_description:0b,immovable:1b,pose:\"crouching\"}"
}

fun Function.mannequinEntityTests() {
	val uuid = UUIDArgument(Uuid.parse("00000000-0000-0000-0000-000000000001"))
	val entity = MannequinEntity(uuid)

	entity.kill() assertsIs "kill @e[limit=1,nbt={UUID:[I;0,0,0,1]},type=minecraft:mannequin]"
	entity.swing(SwingHand.MAINHAND) assertsIs "swing @e[limit=1,nbt={UUID:[I;0,0,0,1]},type=minecraft:mannequin] mainhand"
	entity.swing(SwingHand.OFFHAND) assertsIs "swing @e[limit=1,nbt={UUID:[I;0,0,0,1]},type=minecraft:mannequin] offhand"
}

fun Function.mannequinSummonTests() {
	val m = mannequin { pose = MannequinPose.STANDING }
	val entity = m.summon(vec3(0, 0, 0))

	lines[0] shouldMatch Regex("""summon minecraft:mannequin 0\.0 0\.0 0\.0 \{pose:"standing",UUID:\[I;.*?\]\}""")
	entity.kill()
	lines[1] shouldMatch Regex("""kill @e\[limit=1,nbt=\{UUID:\[I;.*?\]\},type=minecraft:mannequin\]""")
}

class MannequinTests : FunSpec({
	test("mannequin nbt") {
		dataPack("helpers_tests") {
			load { mannequinTests() }
		}.generate()
	}

	test("mannequin entity selector") {
		dataPack("helpers_tests") {
			load { mannequinEntityTests() }
		}.generate()
	}

	test("mannequin summon") {
		dataPack("helpers_tests") {
			load { mannequinSummonTests() }
		}.generate()
	}
})
