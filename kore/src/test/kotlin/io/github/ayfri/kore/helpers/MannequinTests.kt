package io.github.ayfri.kore.helpers

import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.helpers.mannequins.*

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
