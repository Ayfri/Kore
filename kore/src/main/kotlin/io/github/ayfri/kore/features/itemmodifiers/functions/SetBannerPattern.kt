package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.data.item.BannerPattern
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

@Serializable
data class SetBannerPattern(
	override var conditions: PredicateAsList? = null,
	var patterns: List<BannerPattern>,
	var append: Boolean? = null,
) : ItemFunction()

fun ItemModifier.setBannerPattern(append: Boolean? = null, patterns: MutableList<BannerPattern>.() -> Unit = {}) =
	SetBannerPattern(patterns = buildList(patterns), append = append).also { modifiers += it }
