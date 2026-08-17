package io.github.ayfri.kore.features.worldgen.templatepool.elements

import io.github.ayfri.kore.features.worldgen.templatepool.TemplatePoolEntry

/**
 * Builder scope for declaring the weighted entries of a template pool.
 *
 * Every weighted element builder (e.g. [single], [feature], [list]) is an extension on this interface, so they only
 * resolve inside a `templatePool { }` block.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Template_pool
 *
 * @property elements The entries appended so far, in declaration order.
 */
interface PoolEntriesScope {
	val elements: MutableList<TemplatePoolEntry>
}
