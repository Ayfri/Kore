package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.rootprovider

import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProviderScope
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.SimpleBlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.Tree
import io.github.ayfri.kore.features.worldgen.intproviders.ConstantIntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Places the arching aerial roots of the mangrove trees, raising the trunk above them.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 *
 * @property rootProvider The block states making up the roots, mangrove roots in vanilla.
 * @property trunkOffsetY How far the trunk is moved upwards, `1` to `3` for vanilla mangroves.
 * @property aboveRootPlacement The blocks placed on top of the roots, a moss carpet half of the time for vanilla mangroves,
 * none when `null`.
 * @property mangroveRootPlacement The shape of the roots and the blocks they turn into over mud.
 */
@Serializable
data class MangroveRootPlacer(
	override var rootProvider: BlockStateProvider = SimpleBlockStateProvider(),
	override var trunkOffsetY: IntProvider = ConstantIntProvider(0),
	override var aboveRootPlacement: AboveRootPlacement? = null,
	var mangroveRootPlacement: MangroveRootPlacement = MangroveRootPlacement(),
) : RootPlacer()

/**
 * The shape of the roots placed by a [MangroveRootPlacer] and the blocks they turn into over mud.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 *
 * @property maxRootWidth Maximum width of the roots, `1` to `12`, `8` for vanilla mangroves.
 * @property maxRootLength Maximum length of the roots, `1` to `64`, `15` for vanilla mangroves.
 * @property randomSkewChance Chance of a root skewing off its course, `0.0` to `1.0`, `0.2` for vanilla mangroves.
 * @property canGrowThrough A block, a block tag or a list of blocks the roots can grow through.
 * @property muddyRootsIn A block, a block tag or a list of blocks that turn the roots into [muddyRootsProvider] when they
 * grow in them, mud and muddy mangrove roots for vanilla mangroves.
 * @property muddyRootsProvider The block states the roots turn into inside [muddyRootsIn].
 */
@Serializable
data class MangroveRootPlacement(
	var maxRootWidth: Int = 0,
	var maxRootLength: Int = 0,
	var randomSkewChance: Double = 0.0,
	var canGrowThrough: InlinableList<BlockOrTagArgument> = emptyList(),
	var muddyRootsIn: InlinableList<BlockOrTagArgument> = emptyList(),
	var muddyRootsProvider: BlockStateProvider = SimpleBlockStateProvider(),
) : BlockStateProviderScope

/**
 * Creates a [MangroveRootPlacer] configured in [block], to assign to [Tree.rootPlacer].
 *
 * The block state provider and int provider builders are scoped to [block].
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
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 */
fun mangroveRootPlacer(block: MangroveRootPlacer.() -> Unit = {}) = MangroveRootPlacer().apply(block)

/**
 * Configures [MangroveRootPlacer.mangroveRootPlacement], the shape of the roots and the blocks they turn into.
 *
 * The block state provider builders are scoped to [block].
 *
 * ```kotlin
 * mangroveRootPlacer {
 *     mangroveRootPlacement {
 *         maxRootWidth = 8
 *         maxRootLength = 15
 *         randomSkewChance = 0.2
 *         muddyRootsIn = listOf(Blocks.MUD, Blocks.MUDDY_MANGROVE_ROOTS)
 *     }
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 */
fun MangroveRootPlacer.mangroveRootPlacement(block: MangroveRootPlacement.() -> Unit = {}) =
	mangroveRootPlacement.apply(block)
