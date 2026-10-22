package io.github.ayfri.kore.features.worldgen.placedfeature.modifiers

import io.github.ayfri.kore.features.worldgen.heightproviders.HeightProviderScope
import io.github.ayfri.kore.features.worldgen.intproviders.IntProviderScope

/**
 * Builder scope for declaring placement modifiers.
 *
 * Every placement modifier builder (e.g. [count], [inSquare], [randomlySelected]) is an extension on this interface, so
 * they only resolve inside a `placedFeature { }` block, the `placed(...) { }` block of an inline placed feature, or a nested
 * block such as [randomlySelected]'s.
 *
 * @property placementModifiers The placement modifiers appended so far, in application order.
 */
interface PlacementModifiersScope : HeightProviderScope, IntProviderScope {
	var placementModifiers: List<PlacementModifier>
}

/**
 * Standalone [PlacementModifiersScope] backing the nested placement modifier blocks, such as [randomlySelected]'s.
 *
 * @property placementModifiers The placement modifiers appended so far, in application order.
 */
internal class PlacementModifiersBuilder : PlacementModifiersScope {
	override var placementModifiers = emptyList<PlacementModifier>()
}

/** Collects the placement modifiers appended in [block] into a list. */
internal fun buildPlacementModifiers(block: PlacementModifiersScope.() -> Unit) =
	PlacementModifiersBuilder().apply(block).placementModifiers
