package io.github.ayfri.kore.features.worldgen.noisesettings.rules

import io.github.ayfri.kore.features.worldgen.verticalanchors.VerticalAnchorScope

/**
 * Builder scope for declaring material rules via [materialRules].
 *
 * Every rule and condition builder is an extension on this class, so they only resolve inside a `materialRules { }`
 * block instead of being visible everywhere.
 *
 * @property rules The rules appended so far, in evaluation order.
 */
class MaterialRulesScope : VerticalAnchorScope {
	val rules = mutableListOf<MaterialRule>()
}

@Deprecated("Renamed to MaterialRulesScope, like the game's registry since 26.3.", ReplaceWith("MaterialRulesScope"))
typealias SurfaceRulesScope = MaterialRulesScope

/** Collects the rules appended in [block] into a list. */
internal fun buildMaterialRules(block: MaterialRulesScope.() -> Unit) = MaterialRulesScope().apply(block).rules
