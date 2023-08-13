package features.worldgen.templatepool

import DataPack
import Generator
import arguments.types.resources.worldgen.TemplatePoolArgument
import generated.TemplatePools
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString

@Serializable
data class TemplatePool(
	@Transient
	override var fileName: String = "template_pool",
	var fallback: TemplatePoolArgument = TemplatePools.Empty,
	var elements: List<TemplatePoolEntry> = emptyList(),
) : Generator {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

fun DataPack.templatePool(fileName: String = "template_pool", block: TemplatePool.() -> Unit): TemplatePoolArgument {
	templatePools += TemplatePool().apply(block)
	return TemplatePoolArgument(fileName, name)
}

fun TemplatePool.elements(block: MutableList<TemplatePoolEntry>.() -> Unit) {
	elements = buildList(block)
}
