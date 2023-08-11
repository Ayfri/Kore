package features.worldgen

import DataPack
import features.worldgen.densityfunction.densityFunction
import features.worldgen.densityfunction.types.*
import generated.DensityFunctions
import generated.Noises
import utils.assertsIs

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
}
