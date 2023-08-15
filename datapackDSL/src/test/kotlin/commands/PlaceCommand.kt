package commands

import arguments.maths.vec3
import arguments.numbers.rot
import arguments.types.literals.rotation
import functions.Function
import generated.ConfiguredFeatures
import generated.ConfiguredStructures
import generated.Structures
import generated.TemplatePools
import utils.assertsIs

fun Function.placeTests() {
	placeFeature(ConfiguredFeatures.BONUS_CHEST, vec3()) assertsIs "place feature minecraft:bonus_chest ~ ~ ~"
	placeJigsaw(TemplatePools.AncientCity.SCULK, "baz", 5, vec3()) assertsIs "place jigsaw minecraft:ancient_city/sculk baz 5 ~ ~ ~"
	placeStructure(ConfiguredStructures.MINESHAFT, vec3()) assertsIs "place structure minecraft:mineshaft ~ ~ ~"

	placeTemplate(
		Structures.Fossil.SKULL_1,
		vec3(),
		rotation(90.rot, 180.rot)
	) assertsIs "place template minecraft:fossil/skull_1 ~ ~ ~ 90 180"
}
