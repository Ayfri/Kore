package io.github.ayfri.kore.features.worldgen.templatepool

import io.github.ayfri.kore.features.worldgen.templatepool.elements.TemplatePoolElement
import kotlinx.serialization.Serializable

/**
 * A weighted entry of a [TemplatePool].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Template_pool
 *
 * @property weight How likely this element is to be picked, between 1 and 150 inclusive.
 * @property element The element placed when this entry is picked.
 */
@Serializable
data class TemplatePoolEntry(
	var weight: Int = 1,
	var element: TemplatePoolElement,
)
