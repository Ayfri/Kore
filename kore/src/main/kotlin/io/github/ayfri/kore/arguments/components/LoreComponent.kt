package io.github.ayfri.kore.arguments.components

data class LoreComponent(
	val loreLines: MutableList<String> = mutableListOf(),
) : Component()

fun Components.lore(block: LoreComponent.() -> Unit) = apply { components += LoreComponent().apply(block) }

fun LoreComponent.line(line: String) = apply { loreLines += line }
