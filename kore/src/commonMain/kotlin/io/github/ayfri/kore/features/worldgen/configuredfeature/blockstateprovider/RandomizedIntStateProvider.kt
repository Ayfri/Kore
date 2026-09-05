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
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#randomized_int_state_provider
 *
 * @property property The name of the integer block state property to override, e.g. `age`.
 * @property values The values [property] is sampled from.
 * @property source The provider giving the block state [property] is applied to.
 */
@Serializable
data class RandomizedIntStateProvider(
	var property: String = "",
	var values: IntProvider = ConstantIntProvider(0),
	var source: BlockStateProvider = SimpleStateProvider(),
) : BlockStateProvider(), BlockStateProviderScope, IntProviderScope

/**
 * Creates a `randomized_int_state_provider`, overriding the integer [property] of the state given by [source].
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
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#randomized_int_state_provider
 */
fun BlockStateProviderScope.randomizedIntStateProvider(
	property: String = "",
	values: IntProvider = ConstantIntProvider(0),
	block: RandomizedIntStateProvider.() -> Unit = {},
) = RandomizedIntStateProvider(property, values).apply(block)

/**
 * Sets [RandomizedIntStateProvider.values] to a uniform int provider between [min] and [max].
 *
 * ```kotlin
 * randomizedIntStateProvider("age") { values(0, 7) }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#randomized_int_state_provider
 */
fun RandomizedIntStateProvider.values(min: Int, max: Int) {
	values = uniform(min, max)
}
