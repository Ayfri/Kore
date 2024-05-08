package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.components.types.WrittenPage
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.itemmodifiers.types.Mode
import io.github.ayfri.kore.features.itemmodifiers.types.ModeHandler
import io.github.ayfri.kore.features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

@Serializable
data class SetWrittenBookPages(
	override var conditions: PredicateAsList? = null,
	var pages: List<WrittenPage> = emptyList(),
) : ItemFunction(), ModeHandler {
	@Serializable
	override lateinit var mode: Mode

	@Serializable
	override var offset: Int? = null

	@Serializable
	override var size: Int? = null
}

fun ItemModifier.setWrittenBookPages(
	pages: List<WrittenPage> = emptyList(),
	block: SetWrittenBookPages.() -> Unit = {},
) = SetWrittenBookPages(pages = pages).also {
	this.modifiers += it.apply(block)
}

fun SetWrittenBookPages.page(text: ChatComponents, filtered: ChatComponents? = null) = apply {
	pages += WrittenPage(text, filtered)
}
