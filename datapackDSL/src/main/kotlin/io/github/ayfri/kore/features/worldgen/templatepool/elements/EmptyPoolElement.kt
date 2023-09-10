package io.github.ayfri.kore.features.worldgen.templatepool.elements

import io.github.ayfri.kore.features.worldgen.templatepool.TemplatePoolEntry
import kotlinx.serialization.Serializable

@Serializable
data object EmptyPoolElement : TemplatePoolElement()

fun MutableList<TemplatePoolEntry>.empty(weight: Int = 0) = run { this += TemplatePoolEntry(weight, EmptyPoolElement) }

fun MutableList<TemplatePoolElement>.empty() = run { this += EmptyPoolElement }
