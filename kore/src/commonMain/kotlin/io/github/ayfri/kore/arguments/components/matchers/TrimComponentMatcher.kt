package io.github.ayfri.kore.arguments.components.matchers

import io.github.ayfri.kore.features.predicates.sub.item.ItemStackSubPredicates
import io.github.ayfri.kore.generated.arguments.TrimMaterialOrTagArgument
import io.github.ayfri.kore.generated.arguments.TrimPatternOrTagArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class TrimComponentMatcher(
	var material: InlinableList<TrimMaterialOrTagArgument>? = null,
	var pattern: InlinableList<TrimPatternOrTagArgument>? = null,
) : ComponentMatcher()

fun ItemStackSubPredicates.trim(init: TrimComponentMatcher.() -> Unit) =
	apply { matchers += TrimComponentMatcher().apply(init) }

fun TrimComponentMatcher.materials(vararg materials: TrimMaterialOrTagArgument) {
	material = materials.toList()
}

fun TrimComponentMatcher.patterns(vararg patterns: TrimPatternOrTagArgument) {
	pattern = patterns.toList()
}
