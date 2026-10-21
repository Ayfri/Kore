package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.maths.vec3
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.data.block.blockState
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.simpleStateProvider
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.simpleBlock
import io.github.ayfri.kore.generated.Blocks
import io.github.ayfri.kore.generated.Features
import io.github.ayfri.kore.generated.ConfiguredStructures
import io.github.ayfri.kore.generated.Structures
import io.github.ayfri.kore.generated.TemplatePools
import io.github.ayfri.kore.generated.arguments.types.JigsawArgument
import io.kotest.core.spec.style.FunSpec

fun Function.placeTests() {
	placeFeature(Features.BONUS_CHEST, vec3()) assertsIs "place feature minecraft:bonus_chest ~ ~ ~"
	placeJigsaw(
		TemplatePools.AncientCity.SCULK,
		JigsawArgument("baz"),
		5,
		vec3()
	) assertsIs "place jigsaw minecraft:ancient_city/sculk minecraft:baz 5 ~ ~ ~"
	placeJigsaw(
		TemplatePools.AncientCity.SCULK,
		"baz",
		5,
		vec3()
	) assertsIs "place jigsaw minecraft:ancient_city/sculk baz 5 ~ ~ ~"
	placeStructure(ConfiguredStructures.MINESHAFT, vec3()) assertsIs "place structure minecraft:mineshaft ~ ~ ~"

	placeTemplate(
		Structures.Fossil.SKULL_1,
		vec3(),
		TemplateRotation.CLOCKWISE_90
	) assertsIs "place template minecraft:fossil/skull_1 ~ ~ ~ clockwise_90"

	placeTemplate(
		Structures.Fossil.SKULL_1,
		vec3(),
		TemplateRotation.CLOCKWISE_180,
		TemplateMirror.FRONT_BACK
	) assertsIs "place template minecraft:fossil/skull_1 ~ ~ ~ 180 front_back"

	placeTemplate(
		Structures.Fossil.SKULL_1,
		vec3(),
		TemplateRotation.NONE,
		strict = true
	) assertsIs "place template minecraft:fossil/skull_1 ~ ~ ~ none strict"

	placeTemplate(
		Structures.Fossil.SKULL_1,
		strict = true
	) assertsIs "place template minecraft:fossil/skull_1 strict"
}

class PlaceCommandTests : FunSpec({
	test("place") {
		dataPack("unit_tests") {
			load { placeTests() }
		}
	}

	test("place inline feature") {
		dataPack("unit_tests") {
			load {
				placeFeature(vec3(0, 64, 0)) {
					simpleBlock("inline", scheduleTick = true) { toPlace = simpleStateProvider(blockState(Blocks.DIAMOND_BLOCK)) }
				} assertsIs """place feature {type:"minecraft:simple_block",to_place:{type:"minecraft:simple_state_provider",state:"minecraft:diamond_block"},schedule_tick:1b} 0 64 0"""
			}
		}
	}
})
