package io.github.ayfri.kore.features.worldgen.placedfeature.modifiers

import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicate
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicateScope
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicatesScope
import io.github.ayfri.kore.features.worldgen.blockpredicate.True
import io.github.ayfri.kore.features.worldgen.blockpredicate.blockPredicate
import io.github.ayfri.kore.features.worldgen.intproviders.ConstantIntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProviderScope
import io.github.ayfri.kore.features.worldgen.placedfeature.PlacedFeature
import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

/**
 * Placement modifier walking the world in [directionOfSearchDirection] until [targetCondition] passes, moving the
 * position to the block found, or discarding it when nothing is found within [maxSteps].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Placed_feature#environment_scan
 *
 * @property directionOfSearchDirection The direction the scan walks in.
 * @property maxSteps The amount of blocks the scan walks through at most, between `1` and `32`.
 * @property targetCondition The condition a block has to pass to stop the scan.
 * @property allowedSearchCondition The condition every block walked through has to pass, always passing when `null`.
 */
@Serializable
data class EnvironmentScan(
	var directionOfSearchDirection: SearchDirection,
	var maxSteps: IntProvider = ConstantIntProvider(0),
	var targetCondition: BlockPredicate = True,
	var allowedSearchCondition: BlockPredicate? = null,
) : PlacementModifier(), BlockPredicateScope, IntProviderScope

/** The direction an [EnvironmentScan] walks in. */
@Serializable(with = SearchDirection.Companion.DirectionSerializer::class)
enum class SearchDirection {
	UP,
	DOWN;

	companion object {
		data object DirectionSerializer : LowercaseSerializer<SearchDirection>(entries)
	}
}

/**
 * Appends an `environment_scan` placement modifier, walking in [directionOfSearchDirection] until
 * [EnvironmentScan.targetCondition] passes.
 *
 * The block predicate builders are scoped to [block].
 *
 * ```kotlin
 * placedFeature("my_feature", ConfiguredFeatures.ACACIA) {
 *     environmentScan(SearchDirection.DOWN, maxSteps = constant(12)) {
 *         targetCondition { solid() }
 *         allowedSearchCondition { replaceable() }
 *     }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Placed_feature#environment_scan
 */
fun PlacedFeature.environmentScan(
	directionOfSearchDirection: SearchDirection,
	maxSteps: IntProvider = ConstantIntProvider(0),
	block: EnvironmentScan.() -> Unit = {},
) {
	placementModifiers += EnvironmentScan(directionOfSearchDirection, maxSteps).apply(block)
}

/**
 * Sets [EnvironmentScan.targetCondition] to the predicate built in [block], the condition stopping the scan.
 *
 * A lone predicate is used as-is, several of them are wrapped in an `all_of`.
 *
 * ```kotlin
 * environmentScan(SearchDirection.DOWN, maxSteps = constant(12)) {
 *     targetCondition { solid() }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate
 */
fun EnvironmentScan.targetCondition(block: BlockPredicatesScope.() -> Unit) {
	targetCondition = blockPredicate(block)
}

/**
 * Sets [EnvironmentScan.allowedSearchCondition] to the predicate built in [block], the condition every block walked
 * through has to pass.
 *
 * A lone predicate is used as-is, several of them are wrapped in an `all_of`.
 *
 * ```kotlin
 * environmentScan(SearchDirection.DOWN, maxSteps = constant(12)) {
 *     allowedSearchCondition { replaceable() }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate
 */
fun EnvironmentScan.allowedSearchCondition(block: BlockPredicatesScope.() -> Unit) {
	allowedSearchCondition = blockPredicate(block)
}
