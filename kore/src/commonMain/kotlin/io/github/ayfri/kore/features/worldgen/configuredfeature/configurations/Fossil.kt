package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import io.github.ayfri.kore.generated.arguments.worldgen.types.ProcessorListArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.StructureArgument
import kotlinx.serialization.Serializable

/**
 * Configuration for the `fossil` feature.
 *
 * Places a fossil structure picked from [fossilStructures], then the overlay structure of the same index from
 * [overlayStructures] on top of it, which carries the ore. Vanilla buries the coal fossils (`fossil_upper`) and the
 * diamond fossils (`fossil_lower`) in the deserts and swamps with it.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#fossil
 *
 * @property maxEmptyCornersAllowed How many corners of the structure can be exposed to air for it to generate, `0` to
 * `7`: lowering it keeps the fossils from floating.
 * @property fossilStructures The fossil structure templates to choose from, not empty.
 * @property overlayStructures The overlay structure templates, as many as [fossilStructures] in the same order.
 * @property fossilProcessors The processor list applied to the fossil structure.
 * @property overlayProcessors The processor list applied to the overlay structure.
 */
@Serializable
data class Fossil(
	var maxEmptyCornersAllowed: Int = 0,
	var fossilStructures: List<StructureArgument> = emptyList(),
	var overlayStructures: List<StructureArgument> = emptyList(),
	var fossilProcessors: ProcessorListArgument,
	var overlayProcessors: ProcessorListArgument,
) : FeatureConfig()

/**
 * Creates a `fossil` configured feature pairing each of [fossilStructures] with the [overlayStructures] of the same
 * index.
 *
 * ```kotlin
 * fossil(
 *     "fossil_coal",
 *     maxEmptyCornersAllowed = 4,
 *     fossilStructures = listOf(StructureArgument("fossil/skull_1")),
 *     overlayStructures = listOf(StructureArgument("fossil/skull_1_coal")),
 *     fossilProcessors = ProcessorLists.FOSSIL_ROT,
 *     overlayProcessors = ProcessorLists.FOSSIL_COAL,
 * )
 * ```
 *
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#fossil
 */
fun ConfiguredFeatures.fossil(
	fileName: String,
	maxEmptyCornersAllowed: Int = 0,
	fossilStructures: List<StructureArgument> = emptyList(),
	overlayStructures: List<StructureArgument> = emptyList(),
	fossilProcessors: ProcessorListArgument,
	overlayProcessors: ProcessorListArgument,
) = dp.configuredFeature(
	fileName,
	Fossil(maxEmptyCornersAllowed, fossilStructures, overlayStructures, fossilProcessors, overlayProcessors),
)
