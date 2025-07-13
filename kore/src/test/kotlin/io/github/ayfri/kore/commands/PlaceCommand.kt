package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.arguments.numbers.rot
import io.github.ayfri.kore.arguments.types.literals.rotation
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.ConfiguredFeatures
import io.github.ayfri.kore.generated.ConfiguredStructures
import io.github.ayfri.kore.generated.Structures
import io.github.ayfri.kore.generated.TemplatePools

fun Function.placeTests() {
	placeFeature(ConfiguredFeatures.BONUS_CHEST, vec3()) assertsIs "place feature minecraft:bonus_chest ~ ~ ~"
	placeJigsaw(TemplatePools.AncientCity.SCULK, "baz", 5, vec3()) assertsIs "place jigsaw minecraft:ancient_city/sculk baz 5 ~ ~ ~"
	placeStructure(ConfiguredStructures.MINESHAFT, vec3()) assertsIs "place structure minecraft:mineshaft ~ ~ ~"

	placeTemplate(
		Structures.Fossil.SKULL_1,
		vec3(),
		rotation(90.rot, 180.rot)
	) assertsIs "place template minecraft:fossil/skull_1 ~ ~ ~ 90 180"

	placeTemplate(
		Structures.Fossil.SKULL_1,
		vec3(),
		rotation(90.rot, 180.rot),
		strict = true
	) assertsIs "place template minecraft:fossil/skull_1 ~ ~ ~ 90 180 strict"

	placeTemplate(
		Structures.Fossil.SKULL_1,
		strict = true
	) assertsIs "place template minecraft:fossil/skull_1 strict"
}
