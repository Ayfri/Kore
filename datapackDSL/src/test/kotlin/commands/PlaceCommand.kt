package commands

import arguments.maths.vec3
import functions.Function
import generated.ConfiguredStructures
import utils.assertsIs

fun Function.placeTests() {
	placeFeature("foo", vec3()) assertsIs "place feature foo ~ ~ ~"
	placeJigsaw("bar", "baz", 5, vec3()) assertsIs "place jigsaw bar baz 5 ~ ~ ~"
	placeStructure(ConfiguredStructures.MINESHAFT, vec3()) assertsIs "place structure minecraft:mineshaft ~ ~ ~"
	placeTemplate("foo", vec3()) assertsIs "place template foo ~ ~ ~"
}
