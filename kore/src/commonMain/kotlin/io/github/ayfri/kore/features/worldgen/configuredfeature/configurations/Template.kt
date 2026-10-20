package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.arguments.StructureRotation
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import io.github.ayfri.kore.generated.arguments.worldgen.types.StructureArgument
import kotlinx.serialization.Serializable

/**
 * A structure template placed by a [Template], with the rotations it can be placed in.
 *
 * @property id The structure template to place.
 * @property rotations The rotations to pick from, applied around the origin, every allowed rotation when `null`.
 */
@Serializable
data class StructureTemplateEntry(
	var id: StructureArgument,
	var rotations: List<StructureRotation>? = null,
)

/**
 * A [StructureTemplateEntry] with its weight in a [Template].
 *
 * @property data The structure template entry.
 * @property weight Weight of the entry, relative to the other entries of the template.
 */
@Serializable
data class WeightedStructureTemplateEntry(
	var data: StructureTemplateEntry,
	var weight: Int,
)

/**
 * Configuration for the `template` feature.
 *
 * Places one of the structure templates, picked by weight, at the placement position. Vanilla places the sulfur
 * springs with it.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#template
 *
 * @property templates The weighted structure templates to pick from.
 */
@Serializable
data class Template(
	var templates: List<WeightedStructureTemplateEntry>,
) : FeatureConfig()

/**
 * Creates a [WeightedStructureTemplateEntry] placing the structure template [id] with the given [weight], in one of
 * [rotations].
 *
 * ```kotlin
 * structureTemplate(Structures.EndCity.BRIDGE_END, weight = 2, rotations = listOf(StructureRotation.CLOCKWISE_90))
 * ```
 */
fun structureTemplate(
	id: StructureArgument,
	weight: Int = 1,
	rotations: List<StructureRotation>? = null,
) = WeightedStructureTemplateEntry(StructureTemplateEntry(id, rotations), weight)

/**
 * Creates a `template` configured feature placing one of the weighted [templates].
 *
 * ```kotlin
 * template(
 *     "end_city_bridges",
 *     structureTemplate(Structures.EndCity.BRIDGE_END, weight = 2),
 *     structureTemplate(Structures.EndCity.BRIDGE_GENTLE_STAIRS),
 * )
 * ```
 *
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#template
 */
fun ConfiguredFeatures.template(fileName: String, vararg templates: WeightedStructureTemplateEntry) =
	dp.configuredFeature(fileName, Template(templates.toList()))

/** Creates a `template` configured feature placing one of the weighted [templates]. */
fun ConfiguredFeatures.template(fileName: String, templates: List<WeightedStructureTemplateEntry>) =
	dp.configuredFeature(fileName, Template(templates))
