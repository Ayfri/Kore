package io.github.ayfri.kore.features.worldgen.templatepool.elements

import io.github.ayfri.kore.features.worldgen.templatepool.Projection
import io.github.ayfri.kore.features.worldgen.templatepool.TemplatePoolEntry
import kotlinx.serialization.Serializable

/**
 * Places several elements at the same position, in order. Generation stops if one of them fails, and the piece cannot
 * grow children.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Template_pool
 *
 * @property projection How the piece adapts to the terrain.
 * @property elements The elements placed together, in placement order.
 */
@Serializable
data class ListPoolElement(
	var projection: Projection = Projection.RIGID,
	override val elements: MutableList<TemplatePoolElement> = mutableListOf(),
) : TemplatePoolElement(), PoolElementsScope

/**
 * Appends a `list_pool_element` entry with the elements declared in [block].
 *
 * ```kotlin
 * list(weight = 2) {
 *     single(Structures.Village.Plains.Houses.PLAINS_SMALL_HOUSE_1)
 *     feature(PlacedFeatures.PILE_HAY)
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Template_pool
 */
fun PoolEntriesScope.list(
	weight: Int = 1,
	projection: Projection = Projection.RIGID,
	block: ListPoolElement.() -> Unit = {},
) = apply { elements += TemplatePoolEntry(weight, ListPoolElement(projection).apply(block)) }

/**
 * Appends a `list_pool_element` with the elements declared in [block] to the enclosing [ListPoolElement].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Template_pool
 */
fun PoolElementsScope.list(
	projection: Projection = Projection.RIGID,
	block: ListPoolElement.() -> Unit = {},
) = apply { elements += ListPoolElement(projection).apply(block) }
