package features.worldgen.templatepool.elements

import features.worldgen.templatepool.TemplatePoolEntry
import kotlinx.serialization.Serializable

@Serializable
data object EmptyPoolElement : TemplatePoolElement()

fun MutableList<TemplatePoolEntry>.empty(weight: Int = 0) = run { this += TemplatePoolEntry(weight, EmptyPoolElement) }

fun MutableList<TemplatePoolElement>.empty() = run { this += EmptyPoolElement }
