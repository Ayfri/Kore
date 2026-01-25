package io.github.ayfri.kore.helpers

import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.helpers.mannequins.*

fun Function.mannequinTests() {
	val m = mannequin {
		playerProfile("Ayfri")
		hiddenLayers(MannequinLayer.CAPE, MannequinLayer.HAT)
		mainHand = MannequinHand.LEFT
	}

	m.toNbt().toString() assertsIs "{hidden_layers:[\"cape\",\"hat\"],main_hand:\"left\",profile:{name:\"Ayfri\"}}"

	val m2 = mannequin {
		textureProfile("tex") {
			model = MannequinModel.SLIM
		}
	}

	m2.toNbt().toString() assertsIs "{profile:{model:\"slim\",texture:\"tex\"}}"
}
