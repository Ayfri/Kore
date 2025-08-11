package io.github.ayfri.kore.features.worldgen.templatepool

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.generated.TemplatePools
import io.github.ayfri.kore.generated.arguments.worldgen.types.TemplatePoolArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString

/**
 * Data-driven jigsaw template pool.
 *
 * Defines a set of structure pieces with weights and a fallback pool. Used by jigsaw placement
 * to assemble villages, pillager outposts, and custom modular structures.
 *
 * JSON format reference: https://minecraft.wiki/w/Template_pool
 */
@Serializable
data class TemplatePool(
	@Transient
	override var fileName: String = "template_pool",
	var fallback: TemplatePoolArgument = TemplatePools.Empty,
	var elements: List<TemplatePoolEntry> = emptyList(),
) : Generator("worldgen/template_pool") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

/**
 * Creates a template pool using a builder block.
 *
 * Produces `data/<namespace>/worldgen/template_pool/<fileName>.json`.
 *
 * JSON format reference: https://minecraft.wiki/w/Template_pool
 */
fun DataPack.templatePool(fileName: String = "template_pool", init: TemplatePool.() -> Unit = {}): TemplatePoolArgument {
	val templatePool = TemplatePool(fileName).apply(init)
	templatePools += templatePool
	return TemplatePoolArgument(fileName, templatePool.namespace ?: name)
}

fun TemplatePool.elements(block: MutableList<TemplatePoolEntry>.() -> Unit) {
	elements = buildList(block)
}
