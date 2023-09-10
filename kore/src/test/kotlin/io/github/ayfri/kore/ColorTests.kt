package io.github.ayfri.kore

import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.colors.color
import io.github.ayfri.kore.assertions.assertsIs

fun colorTests() {
	val red = Color.RED.toRGB()
	val blue = Color.BLUE.toRGB()

	red.hex assertsIs "ff5555"
	blue.hex assertsIs "5555ff"

	red.r assertsIs 255
	red.g assertsIs 85
	red.b assertsIs 85

	red[0] assertsIs 255

	red.array.toList() assertsIs listOf(255, 85, 85)
	red.normalizedArray.toList() assertsIs doubleArrayOf(1.0, 0.3333333333333333, 0.3333333333333333).toList()

	red.grayscale().hex assertsIs "8d8d8d"
	red.invert().hex assertsIs "00aaaa"
	red.mix(blue).hex assertsIs "aa55aa"
	red.mix(blue, 0.25).hex assertsIs "d45580"
	red.mix(blue, 2) assertsIs listOf(color(255, 85, 85), color(171, 86, 171))

	val redARGB = red.toARGB()
	val blueARGB = blue.toARGB(128)
	redARGB.hex assertsIs "ffff5555"
	redARGB.toRGB().hex assertsIs "ff5555"
	redARGB.mix(blueARGB).hex assertsIs "bfaa55aa"
	redARGB.mix(blueARGB, 0.25).hex assertsIs "dfd4547e"
	redARGB.mix(blueARGB, 2) assertsIs listOf(color(255, 85, 85, 255), color(169, 84, 169, 191))

	red.toARGB(100).hex assertsIs "64ff5555"
}
