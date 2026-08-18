package io.github.ayfri.kore.features.worldgen

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.features.worldgen.densityfunction.densityFunctionsBuilder
import io.github.ayfri.kore.features.worldgen.densityfunction.types.*
import io.github.ayfri.kore.generated.DensityFunctions
import io.github.ayfri.kore.generated.Noises
import io.github.ayfri.kore.utils.pretty
import io.kotest.core.spec.style.FunSpec

fun DataPack.densityFunctionTests() {
	densityFunctionsBuilder.abs("abs", DensityFunctions.Overworld.BASE_3D_NOISE)

	densityFunctions.last() assertsIs """
		{
			"type": "minecraft:abs",
			"argument": "minecraft:overworld/base_3d_noise"
		}
	""".trimIndent()

	densityFunctionsBuilder.add("add", DensityFunctions.Overworld.BASE_3D_NOISE, DensityFunctions.Nether.BASE_3D_NOISE)

	densityFunctions.last() assertsIs """
		{
			"type": "minecraft:add",
			"argument1": "minecraft:overworld/base_3d_noise",
			"argument2": "minecraft:nether/base_3d_noise"
		}
	""".trimIndent()

	densityFunctionsBuilder.beardifier("beardifier")

	densityFunctions.last() assertsIs """
		{
			"type": "minecraft:beardifier"
		}
	""".trimIndent()

	densityFunctionsBuilder.blendAlpha("blend_alpha")

	densityFunctions.last() assertsIs """
		{
			"type": "minecraft:blend_alpha"
		}
	""".trimIndent()

	densityFunctionsBuilder.blendDensity("blend_density", DensityFunctions.Overworld.BASE_3D_NOISE)

	densityFunctions.last() assertsIs """
		{
			"type": "minecraft:blend_density",
			"argument": "minecraft:overworld/base_3d_noise"
		}
	""".trimIndent()

	densityFunctionsBuilder.blendOffset("blend_offset")

	densityFunctions.last() assertsIs """
		{
			"type": "minecraft:blend_offset"
		}
	""".trimIndent()

	densityFunctionsBuilder.cache2D("cache_2d", DensityFunctions.Overworld.BASE_3D_NOISE)

	densityFunctions.last() assertsIs """
		{
			"type": "minecraft:cache_2d",
			"argument": "minecraft:overworld/base_3d_noise"
		}
	""".trimIndent()

	densityFunctionsBuilder.cacheAllInCell("cache_all_in_cell", DensityFunctions.Overworld.BASE_3D_NOISE)

	densityFunctions.last() assertsIs """
		{
			"type": "minecraft:cache_all_in_cell",
			"argument": "minecraft:overworld/base_3d_noise"
		}
	""".trimIndent()

	densityFunctionsBuilder.cacheOnce("cache_once", DensityFunctions.Overworld.BASE_3D_NOISE)

	densityFunctions.last() assertsIs """
		{
			"type": "minecraft:cache_once",
			"argument": "minecraft:overworld/base_3d_noise"
		}
	""".trimIndent()

	densityFunctionsBuilder.clamp("clamp", DensityFunctions.Overworld.BASE_3D_NOISE, min = -1.0, max = 1.0)

	densityFunctions.last() assertsIs """
		{
			"type": "minecraft:clamp",
			"input": "minecraft:overworld/base_3d_noise",
			"min": -1.0,
			"max": 1.0
		}
	""".trimIndent()

	densityFunctionsBuilder.constant("constant", 1.0)

	densityFunctions.last() assertsIs """
		{
			"type": "minecraft:constant",
			"argument": 1.0
		}
	""".trimIndent()

	densityFunctionsBuilder.cube("cube", DensityFunctions.Overworld.BASE_3D_NOISE)

	densityFunctions.last() assertsIs """
		{
			"type": "minecraft:cube",
			"argument": "minecraft:overworld/base_3d_noise"
		}
	""".trimIndent()

	densityFunctionsBuilder.endIslands("end_islands")

	densityFunctions.last() assertsIs """
		{
			"type": "minecraft:end_islands"
		}
	""".trimIndent()

	densityFunctionsBuilder.findTopSurface(
		"find_top_surface",
		DensityFunctions.Overworld.BASE_3D_NOISE,
		DensityFunctions.Nether.BASE_3D_NOISE,
		lowerBound = 0,
		cellHeight = 2
	)

	densityFunctions.last() assertsIs """
		{
			"type": "minecraft:find_top_surface",
			"density": "minecraft:overworld/base_3d_noise",
			"upper_bound": "minecraft:nether/base_3d_noise",
			"lower_bound": 0,
			"cell_height": 2
		}
	""".trimIndent()

	densityFunctionsBuilder.flatCache("flat_cache", DensityFunctions.Overworld.BASE_3D_NOISE)

	densityFunctions.last() assertsIs """
		{
			"type": "minecraft:flat_cache",
			"argument": "minecraft:overworld/base_3d_noise"
		}
	""".trimIndent()

	densityFunctionsBuilder.halfNegative("half_negative", DensityFunctions.Overworld.BASE_3D_NOISE)

	densityFunctions.last() assertsIs """
		{
			"type": "minecraft:half_negative",
			"argument": "minecraft:overworld/base_3d_noise"
		}
	""".trimIndent()

	densityFunctionsBuilder.interpolated("interpolated", DensityFunctions.Overworld.BASE_3D_NOISE)

	densityFunctions.last() assertsIs """
		{
			"type": "minecraft:interpolated",
			"argument": "minecraft:overworld/base_3d_noise"
		}
	""".trimIndent()

	densityFunctionsBuilder.intervalSelect("interval_select") {
		input(DensityFunctions.Overworld.BASE_3D_NOISE)
		thresholds(-0.5f, 0.5f)
		function(DensityFunctions.Nether.BASE_3D_NOISE)
		function(0.0)
		function(DensityFunctions.End.BASE_3D_NOISE)
	}

	densityFunctions.last() assertsIs """
		{
			"type": "minecraft:interval_select",
			"input": "minecraft:overworld/base_3d_noise",
			"thresholds": [
				-0.5,
				0.5
			],
			"functions": [
				"minecraft:nether/base_3d_noise",
				0.0,
				"minecraft:end/base_3d_noise"
			]
		}
	""".trimIndent()

	densityFunctionsBuilder.invert("invert", DensityFunctions.Overworld.BASE_3D_NOISE)

	densityFunctions.last() assertsIs """
		{
			"type": "minecraft:invert",
			"argument": "minecraft:overworld/base_3d_noise"
		}
	""".trimIndent()

	densityFunctionsBuilder.max("max", DensityFunctions.Overworld.BASE_3D_NOISE, DensityFunctions.Nether.BASE_3D_NOISE)

	densityFunctions.last() assertsIs """
		{
			"type": "minecraft:max",
			"argument1": "minecraft:overworld/base_3d_noise",
			"argument2": "minecraft:nether/base_3d_noise"
		}
	""".trimIndent()

	densityFunctionsBuilder.min("min", DensityFunctions.Overworld.BASE_3D_NOISE, DensityFunctions.Nether.BASE_3D_NOISE)

	densityFunctions.last() assertsIs """
		{
			"type": "minecraft:min",
			"argument1": "minecraft:overworld/base_3d_noise",
			"argument2": "minecraft:nether/base_3d_noise"
		}
	""".trimIndent()

	densityFunctionsBuilder.mul("mul", DensityFunctions.Overworld.BASE_3D_NOISE, DensityFunctions.Nether.BASE_3D_NOISE)

	densityFunctions.last() assertsIs """
		{
			"type": "minecraft:mul",
			"argument1": "minecraft:overworld/base_3d_noise",
			"argument2": "minecraft:nether/base_3d_noise"
		}
	""".trimIndent()

	densityFunctionsBuilder.noise("noise", Noises.Calcite, xzScale = 0.5, yScale = 0.25)

	densityFunctions.last() assertsIs """
		{
			"type": "minecraft:noise",
			"noise": "minecraft:calcite",
			"xz_scale": 0.5,
			"y_scale": 0.25
		}
	""".trimIndent()

	densityFunctionsBuilder.oldBlendedNoise("old_blended_noise") {
		xzScale = 1.0
		yScale = 1.0
		xzFactor = 80.0
		yFactor = 160.0
		smearScaleMultiplier = 8.0
	}

	densityFunctions.last() assertsIs """
		{
			"type": "minecraft:old_blended_noise",
			"xz_scale": 1.0,
			"y_scale": 1.0,
			"xz_factor": 80.0,
			"y_factor": 160.0,
			"smear_scale_multiplier": 8.0
		}
	""".trimIndent()

	densityFunctionsBuilder.quarterNegative("quarter_negative", DensityFunctions.Overworld.BASE_3D_NOISE)

	densityFunctions.last() assertsIs """
		{
			"type": "minecraft:quarter_negative",
			"argument": "minecraft:overworld/base_3d_noise"
		}
	""".trimIndent()

	densityFunctionsBuilder.rangeChoice("range_choice") {
		input(DensityFunctions.Overworld.BASE_3D_NOISE)
		minInclusive = 0.0
		maxExclusive = 1.0
		whenInRange(1.0)
		whenOutOfRange(DensityFunctions.Nether.BASE_3D_NOISE)
	}

	densityFunctions.last() assertsIs """
		{
			"type": "minecraft:range_choice",
			"input": "minecraft:overworld/base_3d_noise",
			"min_inclusive": 0.0,
			"max_exclusive": 1.0,
			"when_in_range": 1.0,
			"when_out_of_range": "minecraft:nether/base_3d_noise"
		}
	""".trimIndent()

	densityFunctionsBuilder.shift("shift", Noises.Calcite)

	densityFunctions.last() assertsIs """
		{
			"type": "minecraft:shift",
			"argument": "minecraft:calcite"
		}
	""".trimIndent()

	densityFunctionsBuilder.shiftA("shift_a", Noises.Calcite)

	densityFunctions.last() assertsIs """
		{
			"type": "minecraft:shift_a",
			"argument": "minecraft:calcite"
		}
	""".trimIndent()

	densityFunctionsBuilder.shiftB("shift_b", Noises.Calcite)

	densityFunctions.last() assertsIs """
		{
			"type": "minecraft:shift_b",
			"argument": "minecraft:calcite"
		}
	""".trimIndent()

	densityFunctionsBuilder.shiftedNoise("shifted_noise", Noises.Calcite) {
		xzScale = 0.1
		yScale = 1.0
		noise = Noises.Calcite

		shiftX(1.0)
		shiftY(DensityFunctions.Overworld.BASE_3D_NOISE)
		shiftZ(DensityFunctions.Nether.BASE_3D_NOISE)
	}

	densityFunctions.last() assertsIs """
		{
			"type": "minecraft:shifted_noise",
			"noise": "minecraft:calcite",
			"xz_scale": 0.1,
			"y_scale": 1.0,
			"shift_x": 1.0,
			"shift_y": "minecraft:overworld/base_3d_noise",
			"shift_z": "minecraft:nether/base_3d_noise"
		}
	""".trimIndent()

	densityFunctionsBuilder.spline("spline_constant", 1.5f)

	densityFunctions.last() assertsIs """
		{
			"type": "minecraft:spline",
			"spline": 1.5
		}
	""".trimIndent()

	densityFunctionsBuilder.spline("spline", DensityFunctions.Overworld.CONTINENTS) {
		point(-1.1f, 0.044f)
		point(-0.51f, DensityFunctions.Overworld.EROSION, derivative = 0.5f) {
			point(-0.6f, 1.0f)
			point(0.5f, -1.0f)
		}
	}

	densityFunctions.last() assertsIs """
		{
			"type": "minecraft:spline",
			"spline": {
				"coordinate": "minecraft:overworld/continents",
				"points": [
					{
						"location": -1.1,
						"value": 0.044,
						"derivative": 0.0
					},
					{
						"location": -0.51,
						"value": {
							"coordinate": "minecraft:overworld/erosion",
							"points": [
								{
									"location": -0.6,
									"value": 1.0,
									"derivative": 0.0
								},
								{
									"location": 0.5,
									"value": -1.0,
									"derivative": 0.0
								}
							]
						},
						"derivative": 0.5
					}
				]
			}
		}
	""".trimIndent()

	densityFunctionsBuilder.square("square", DensityFunctions.Overworld.BASE_3D_NOISE)

	densityFunctions.last() assertsIs """
		{
			"type": "minecraft:square",
			"argument": "minecraft:overworld/base_3d_noise"
		}
	""".trimIndent()

	densityFunctionsBuilder.squeeze("squeeze", DensityFunctions.Overworld.BASE_3D_NOISE)

	densityFunctions.last() assertsIs """
		{
			"type": "minecraft:squeeze",
			"argument": "minecraft:overworld/base_3d_noise"
		}
	""".trimIndent()

	densityFunctionsBuilder.yClampedGradient("y_clamped_gradient") {
		fromY = -64
		toY = 320
		fromValue = -1.0
		toValue = 1.0
	}

	densityFunctions.last() assertsIs """
		{
			"type": "minecraft:y_clamped_gradient",
			"from_y": -64,
			"to_y": 320,
			"from_value": -1.0,
			"to_value": 1.0
		}
	""".trimIndent()
}

class DensityFunctionTests : FunSpec({
	test("density function") {
		dataPack("densityFunction") {
			pretty()
			densityFunctionTests()
		}
	}
})
