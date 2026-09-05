package io.github.ayfri.kore.features.worldgen.templatepool.elements

/**
 * Builder scope for declaring the unweighted elements nested inside a [ListPoolElement].
 *
 * The weight belongs to the entry holding the list, so these builders take no `weight` parameter. They only resolve
 * inside a `list { }` block.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Template_pool
 *
 * @property elements The elements appended so far, in placement order.
 */
interface PoolElementsScope {
	val elements: MutableList<TemplatePoolElement>
}
