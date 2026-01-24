package io.github.ayfri.kore.features.worldgen

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.features.worldgen.densityfunction.densityFunction
import io.github.ayfri.kore.features.worldgen.densityfunction.types.*
import io.github.ayfri.kore.generated.DensityFunctions
import io.github.ayfri.kore.generated.Noises

fun DataPack.densityFunctionTests() {
	densityFunction(
		"squeeze",
		squeeze(DensityFunctions.Overworld.BASE_3D_NOISE)
	)

	densityFunctions.last() assertsIs """
		{
			"type": "minecraft:squeeze",
			"argument": "minecraft:overworld/base_3d_noise"
		}
	""".trimIndent()

	densityFunction(
		"weird_scaled_sampler",
		weirdScaledSampler(
			RarityValueMapper.TYPE_1,
			Noises.CALCITE,
			DensityFunctions.Overworld.BASE_3D_NOISE
		)
	)

	densityFunctions.last() assertsIs """
		{
			"type": "minecraft:weird_scaled_sampler",
			"rarity_value_mapper": "type_1",
			"noise": "minecraft:calcite",
			"input": "minecraft:overworld/base_3d_noise"
		}
	""".trimIndent()

	densityFunction(
		"weird_scaled_sampler",
		shiftedNoise(
			Noises.CALCITE,
		) {
			xzScale = 0.1
			yScale = 1.0
			noise = Noises.CALCITE

			shiftX(1.0)
			shiftY(DensityFunctions.Overworld.BASE_3D_NOISE)
			shiftZ(DensityFunctions.Nether.BASE_3D_NOISE)
		}
	)

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

	densityFunction("simple", Beardifier)

	densityFunctions.last() assertsIs """
		{
			"type": "minecraft:beardifier"
		}
	""".trimIndent()

	densityFunction(
		"find_top_surface",
		findTopSurface(
			DensityFunctions.Overworld.BASE_3D_NOISE,
			DensityFunctions.Nether.BASE_3D_NOISE,
			lowerBound = 0,
			cellHeight = 2
		)
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

	densityFunction(
		"invert",
		invert(DensityFunctions.Overworld.BASE_3D_NOISE)
	)

	densityFunctions.last() assertsIs """
		{
			"type": "minecraft:invert",
			"argument": "minecraft:overworld/base_3d_noise"
		}
	""".trimIndent()
}
