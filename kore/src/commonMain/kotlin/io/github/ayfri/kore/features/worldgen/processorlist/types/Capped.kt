package io.github.ayfri.kore.features.worldgen.processorlist.types

import io.github.ayfri.kore.features.worldgen.intproviders.ConstantIntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProviderScope
import io.github.ayfri.kore.features.worldgen.intproviders.constant
import kotlinx.serialization.Serializable

/**
 * Runs [delegate] on at most [limit] blocks of the template, picked at random.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list
 *
 * @property limit How many blocks [delegate] is applied to, sampled once per structure piece. Must be positive.
 * @property delegate The processor to apply, which cannot be another [Capped] processor.
 */
@Serializable
data class Capped(
	var limit: IntProvider = ConstantIntProvider(1),
	var delegate: ProcessorType = Nop,
) : ProcessorType(), IntProviderScope

/**
 * Appends a `capped` processor running [delegate] on at most [limit] blocks.
 *
 * [delegate] cannot be another `capped` processor.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list
 */
fun ProcessorsScope.capped(limit: IntProvider, delegate: ProcessorType) = apply { processors += Capped(limit, delegate) }

/**
 * Appends a `capped` processor running [delegate] on at most [limit] blocks.
 *
 * [delegate] cannot be another `capped` processor.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list
 */
fun ProcessorsScope.capped(limit: Int, delegate: ProcessorType) = apply { processors += Capped(constant(limit), delegate) }

/**
 * Appends a `capped` processor running the single processor declared in [block] on at most [limit] blocks.
 *
 * ```kotlin
 * capped(uniform(1, 4)) {
 *     blockAge(0.5)
 * }
 * ```
 *
 * [block] has to declare exactly one processor, and it cannot be another `capped` processor.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list
 */
fun ProcessorsScope.capped(limit: IntProvider, block: ProcessorsScope.() -> Unit) = apply {
	val delegate = buildProcessors(block).singleOrNull() ?: error("A capped processor needs exactly one delegate processor.")
	processors += Capped(limit, delegate)
}

/**
 * Appends a `capped` processor running the single processor declared in [block] on at most [limit] blocks.
 *
 * ```kotlin
 * capped(4) {
 *     blockAge(0.5)
 * }
 * ```
 *
 * [block] has to declare exactly one processor, and it cannot be another `capped` processor.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list
 */
fun ProcessorsScope.capped(limit: Int, block: ProcessorsScope.() -> Unit) = capped(constant(limit), block)
