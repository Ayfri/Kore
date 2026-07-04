package io.github.ayfri.kore.commands

import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.generated.Biomes
import io.github.ayfri.kore.generated.ConfiguredStructures
import io.github.ayfri.kore.generated.PointOfInterestTypes
import io.github.ayfri.kore.generated.Tags
import io.kotest.core.spec.style.FunSpec

fun Function.locateTests() {
	locateBiome(Biomes.PLAINS) assertsIs "locate biome minecraft:plains"
	locateStructure(ConfiguredStructures.MANSION) assertsIs "locate structure minecraft:mansion"
	locateStructure(Tags.Worldgen.Structure.VILLAGE) assertsIs "locate structure #minecraft:village"
	locatePointOfInterest(PointOfInterestTypes.BEE_NEST) assertsIs "locate poi minecraft:bee_nest"
}

class LocateCommandTests : FunSpec({
	test("locate") {
		dataPack("unit_tests") {
			load { locateTests() }
		}.generate()
	}
})
