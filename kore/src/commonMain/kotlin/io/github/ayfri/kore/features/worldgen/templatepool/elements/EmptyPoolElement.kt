package io.github.ayfri.kore.features.worldgen.templatepool.elements

import io.github.ayfri.kore.features.worldgen.templatepool.TemplatePoolEntry
import kotlinx.serialization.Serializable

/**
 * Places nothing, useful as filler weight so a pool sometimes stops growing without falling back.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Template_pool
 */
@Serializable
data object EmptyPoolElement : TemplatePoolElement()

/**
 * Appends an `empty_pool_element` entry with the given [weight].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Template_pool
 */
fun PoolEntriesScope.empty(weight: Int = 1) = apply { elements += TemplatePoolEntry(weight, EmptyPoolElement) }

/**
 * Appends an `empty_pool_element` to the enclosing [ListPoolElement].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Template_pool
 */
fun PoolElementsScope.empty() = apply { elements += EmptyPoolElement }
