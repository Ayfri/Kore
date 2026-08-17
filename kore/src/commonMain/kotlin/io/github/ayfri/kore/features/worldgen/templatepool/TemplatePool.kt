package io.github.ayfri.kore.features.worldgen.templatepool

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.features.worldgen.templatepool.elements.PoolEntriesScope
import io.github.ayfri.kore.generated.TemplatePools
import io.github.ayfri.kore.generated.arguments.worldgen.types.TemplatePoolArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * A jigsaw template pool, a weighted set of structure pieces the jigsaw placement picks from when it grows a
 * structure (villages, pillager outposts, trial chambers, custom modular structures).
 *
 * Produces `data/<namespace>/worldgen/template_pool/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/structures
 * Minecraft Wiki: https://minecraft.wiki/w/Template_pool
 *
 * @property fallback The pool used when a piece cannot connect any further.
 * @property elements The weighted entries to pick from, in declaration order.
 */
@Serializable
data class TemplatePool(
	@Transient
	override var fileName: String = "template_pool",
	var fallback: TemplatePoolArgument = TemplatePools.Empty,
	override val elements: MutableList<TemplatePoolEntry> = mutableListOf(),
) : Generator("worldgen/template_pool"), PoolEntriesScope {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

/**
 * Creates a template pool, appending the entries declared in [init].
 *
 * ```kotlin
 * val houses = templatePool("village/houses") {
 *     fallback = TemplatePools.Empty
 *
 *     single(Structures.Village.Plains.Houses.PLAINS_SMALL_HOUSE_1, weight = 3)
 *     empty(weight = 1)
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/template_pool/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/structures
 * Minecraft Wiki: https://minecraft.wiki/w/Template_pool
 */
fun DataPack.templatePool(fileName: String = "template_pool", init: TemplatePool.() -> Unit = {}): TemplatePoolArgument {
	val templatePool = TemplatePool(fileName).apply(init)
	templatePools += templatePool
	return TemplatePoolArgument(fileName, templatePool.namespace ?: name)
}
