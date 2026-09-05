package io.github.ayfri.kore.features.worldgen.templatepool.elements

import io.github.ayfri.kore.features.worldgen.templatepool.Projection
import io.github.ayfri.kore.features.worldgen.templatepool.TemplatePoolEntry
import io.github.ayfri.kore.generated.arguments.worldgen.types.PlacedFeatureArgument
import kotlinx.serialization.Serializable

/**
 * Places a placed feature instead of a structure template, inside a 1x1x1 piece.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Template_pool
 *
 * @property projection How the piece adapts to the terrain.
 * @property feature The placed feature to generate.
 */
@Serializable
data class FeaturePoolElement(
	var projection: Projection = Projection.RIGID,
	var feature: PlacedFeatureArgument,
) : TemplatePoolElement()

/**
 * Appends a `feature_pool_element` entry generating [feature].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Template_pool
 */
fun PoolEntriesScope.feature(
	feature: PlacedFeatureArgument,
	weight: Int = 1,
	projection: Projection = Projection.RIGID,
	block: FeaturePoolElement.() -> Unit = {},
) = apply { elements += TemplatePoolEntry(weight, FeaturePoolElement(projection, feature).apply(block)) }

/**
 * Appends a `feature_pool_element` generating [feature] to the enclosing [ListPoolElement].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Template_pool
 */
fun PoolElementsScope.feature(
	feature: PlacedFeatureArgument,
	projection: Projection = Projection.RIGID,
	block: FeaturePoolElement.() -> Unit = {},
) = apply { elements += FeaturePoolElement(projection, feature).apply(block) }
