package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.arguments.StructureRotation
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.StructureArgument
import kotlinx.serialization.Serializable

@Serializable
data class StructureTemplateEntry(
	var id: StructureArgument,
	var rotations: List<StructureRotation>? = null,
)

@Serializable
data class WeightedStructureTemplateEntry(
	var data: StructureTemplateEntry,
	var weight: Int,
)

/**
 * Represents the `minecraft:template` configured feature, which places a random weighted structure template.
 */
@Serializable
data class Template(
	var templates: List<WeightedStructureTemplateEntry>,
) : FeatureConfig()

fun structureTemplate(
	id: StructureArgument,
	weight: Int = 1,
	rotations: List<StructureRotation>? = null,
) = WeightedStructureTemplateEntry(StructureTemplateEntry(id, rotations), weight)

fun ConfiguredFeatures.template(fileName: String, vararg templates: WeightedStructureTemplateEntry): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, Template(templates.toList()))
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}

fun ConfiguredFeatures.template(fileName: String, templates: List<WeightedStructureTemplateEntry>): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, Template(templates))
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}
