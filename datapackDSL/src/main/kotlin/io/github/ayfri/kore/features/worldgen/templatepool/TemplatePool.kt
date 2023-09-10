package io.github.ayfri.kore.features.worldgen.templatepool

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.arguments.types.resources.worldgen.TemplatePoolArgument
import io.github.ayfri.kore.generated.TemplatePools
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString

@Serializable
data class TemplatePool(
	@Transient
	override var fileName: String = "template_pool",
	var fallback: TemplatePoolArgument = TemplatePools.Empty,
	var elements: List<TemplatePoolEntry> = emptyList(),
) : Generator("worldgen/template_pool") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

fun DataPack.templatePool(fileName: String = "template_pool", block: TemplatePool.() -> Unit): TemplatePoolArgument {
	templatePools += TemplatePool().apply(block)
	return TemplatePoolArgument(fileName, name)
}

fun TemplatePool.elements(block: MutableList<TemplatePoolEntry>.() -> Unit) {
	elements = buildList(block)
}
