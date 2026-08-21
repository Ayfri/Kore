package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.rootprovider

import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProviderScope
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.SimpleStateProvider
import io.github.ayfri.kore.features.worldgen.intproviders.ConstantIntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Places the arching aerial roots of the mangrove trees.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 *
 * @property rootProvider The block states making up the roots.
 * @property trunkOffsetY The vertical offset applied to the trunk once the roots are placed.
 * @property aboveRootProvider The blocks placed on top of the roots, none when `null`.
 * @property mangroveRootPlacement The shape and the substitutions of the roots.
 */
@Serializable
data class MangroveRootPlacer(
	override var rootProvider: BlockStateProvider = SimpleStateProvider(),
	override var trunkOffsetY: IntProvider = ConstantIntProvider(0),
	override var aboveRootProvider: AboveRootPlacement? = null,
	var mangroveRootPlacement: MangroveRootPlacement = MangroveRootPlacement(),
) : RootPlacer()

/**
 * The shape of the roots placed by a [MangroveRootPlacer] and the blocks they turn into over mud.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 *
 * @property maxRootWidth The maximum horizontal distance a root may reach from the trunk.
 * @property maxRootLength The maximum amount of blocks a single root is made of.
 * @property randomSkewChance The chance of a root stepping sideways, between `0.0` and `1.0`.
 * @property canGrowTrough The blocks the roots may grow through.
 * @property muddyRootsIn The blocks the roots turn into [muddyRootsProvider] in.
 * @property muddyRootsProvider The block states replacing the roots inside [muddyRootsIn].
 */
@Serializable
data class MangroveRootPlacement(
	var maxRootWidth: Int = 0,
	var maxRootLength: Int = 0,
	var randomSkewChance: Double = 0.0,
	var canGrowTrough: InlinableList<BlockOrTagArgument> = emptyList(),
	var muddyRootsIn: InlinableList<BlockOrTagArgument> = emptyList(),
	var muddyRootsProvider: BlockStateProvider = SimpleStateProvider(),
) : BlockStateProviderScope

/**
 * Creates a `mangrove_root_placer`, placing the arching aerial roots of the mangrove trees.
 *
 * The block state provider builders are scoped to [block].
 *
 * ```kotlin
 * tree("mangrove") {
 *     rootPlacer = mangroveRootPlacer {
 *         rootProvider = simpleStateProvider(Blocks.MANGROVE_ROOTS)
 *         trunkOffsetY = uniform(1, 3)
 *         mangroveRootPlacement {
 *             maxRootWidth = 8
 *             maxRootLength = 15
 *             muddyRootsProvider = simpleStateProvider(Blocks.MUDDY_MANGROVE_ROOTS)
 *         }
 *     }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 */
fun mangroveRootPlacer(block: MangroveRootPlacer.() -> Unit = {}) = MangroveRootPlacer().apply(block)

/**
 * Configures [MangroveRootPlacer.mangroveRootPlacement], the shape of the roots and the blocks they turn into.
 *
 * The block state provider builders are scoped to [block].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 */
fun MangroveRootPlacer.mangroveRootPlacement(block: MangroveRootPlacement.() -> Unit = {}) =
	mangroveRootPlacement.apply(block)
