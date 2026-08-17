package io.github.ayfri.kore.features.worldgen.noisesettings.rules

/**
 * Builder scope for declaring surface rules via [surfaceRules].
 *
 * Every rule and condition builder is an extension on this class, so they only resolve inside a `surfaceRules { }`
 * block instead of being visible everywhere.
 *
 * @property rules The rules appended so far, in evaluation order.
 */
class SurfaceRulesScope {
	val rules = mutableListOf<SurfaceRule>()
}

/** Collects the rules appended in [block] into a list. */
internal fun buildSurfaceRules(block: SurfaceRulesScope.() -> Unit) = SurfaceRulesScope().apply(block).rules
