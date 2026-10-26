package io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider

import io.github.ayfri.kore.features.worldgen.intproviders.ConstantIntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProviderScope
import io.github.ayfri.kore.features.worldgen.intproviders.UniformIntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.uniform
import kotlinx.serialization.Serializable

/**
 * Takes the block state given by [source] and overrides its integer [property] with a value sampled from [values].
 *
 * Vanilla uses it to randomize the `age` of the crops and the `berries` of the cave vines. The block placed by
 * [source] has to actually declare [property], the state is left untouched otherwise.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#randomized_int
 *
 * @property property The name of the integer block state property to override, e.g. `age`.
 * @property values The values [property] is sampled from.
 * @property source The provider giving the block state [property] is applied to.
 */
@Serializable
data class RandomizedIntBlockStateProvider(
	var property: String = "",
	var values: IntProvider = ConstantIntProvider(0),
	var source: BlockStateProvider = SimpleBlockStateProvider(),
) : BlockStateProvider(), BlockStateProviderScope, IntProviderScope

/**
 * Creates a `randomized_int` block state provider, overriding the integer [property] of the state given by [source].
 *
 * ```kotlin
 * simpleBlock("random_wheat") {
 *     toPlace = randomizedIntStateProvider("age") {
 *         values(0, 7)
 *         source = simpleStateProvider(Blocks.WHEAT)
 *     }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#randomized_int
 */
fun BlockStateProviderScope.randomizedIntStateProvider(
	property: String = "",
	values: IntProvider = ConstantIntProvider(0),
	block: RandomizedIntBlockStateProvider.() -> Unit = {},
) = RandomizedIntBlockStateProvider(property, values).apply(block)

/**
 * Creates a `randomized_int` block state provider, overriding the integer [property] of the state given by [source]
 * with the fixed [values].
 *
 * ```kotlin
 * randomizedIntStateProvider("age", values = 7) { source = simpleStateProvider(Blocks.WHEAT) }
 * ```
 */
fun BlockStateProviderScope.randomizedIntStateProvider(
	property: String = "",
	values: Int,
	block: RandomizedIntBlockStateProvider.() -> Unit = {},
) = randomizedIntStateProvider(property, ConstantIntProvider(values), block)

/**
 * Sets [RandomizedIntBlockStateProvider.values] to [value], the values the property is sampled from.
 *
 * ```kotlin
 * randomizedIntStateProvider("age") { values(clamped(0, 7, uniform(0, 9))) }
 * ```
 */
fun RandomizedIntBlockStateProvider.values(value: IntProvider) {
	values = value
}

/** Sets [RandomizedIntBlockStateProvider.values] to the constant [value]. */
fun RandomizedIntBlockStateProvider.values(value: Int) = values(ConstantIntProvider(value))

/**
 * Sets [RandomizedIntBlockStateProvider.values] to a uniform int provider between [min] and [max].
 *
 * ```kotlin
 * randomizedIntStateProvider("age") { values(0, 7) }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#randomized_int
 */
fun RandomizedIntBlockStateProvider.values(min: Int, max: Int) {
	values = uniform(min, max)
}
