package io.github.ayfri.kore.features.worldgen.densityfunction

import io.github.ayfri.kore.generated.arguments.worldgen.types.DensityFunctionArgument
import io.github.ayfri.kore.serializers.EitherInlineSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KeepGeneratedSerializer
import kotlinx.serialization.Serializable

/**
 * This class represents a density function or a double value.
 * Needed because of Minecraft [NoiseRouter] properties.
 *
 * @property double The double value. It can be null if the instance represents a density function.
 * @property densityFunction The density function argument. It can be null if the instance represents a double value.
 */
@OptIn(ExperimentalSerializationApi::class)
@KeepGeneratedSerializer
@Serializable(with = DensityFunctionOrDouble.Companion.DensityFunctionOrDoubleSerializer::class)
data class DensityFunctionOrDouble(
	var double: Double? = null,
	var densityFunction: DensityFunctionArgument? = null,
) {
	companion object {
		data object DensityFunctionOrDoubleSerializer :
			EitherInlineSerializer<DensityFunctionOrDouble>(generatedSerializer(), "double", "densityFunction")
	}
}

fun densityFunctionOrDouble(double: Double) = DensityFunctionOrDouble(double = double)
fun densityFunctionOrDouble(densityFunction: DensityFunctionArgument) = DensityFunctionOrDouble(densityFunction = densityFunction)
