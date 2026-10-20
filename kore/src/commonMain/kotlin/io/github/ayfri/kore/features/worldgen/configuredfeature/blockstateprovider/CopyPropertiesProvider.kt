package io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider

import kotlinx.serialization.Serializable

/**
 * Takes the block state given by [source] and copies onto it the properties it shares with the block already at the
 * position.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#copy_properties_provider
 *
 * @property source The provider giving the block state the properties are copied onto.
 */
@Serializable
data class CopyPropertiesProvider(
	var source: BlockStateProvider = SimpleStateProvider(),
) : BlockStateProvider()

/**
 * Creates a `copy_properties_provider`, copying the properties the block already at the position shares with the
 * state given by [source].
 *
 * ```kotlin
 * simpleBlock("waxed_copper") {
 *     toPlace = copyPropertiesProvider(simpleStateProvider(Blocks.WAXED_COPPER_BLOCK))
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#copy_properties_provider
 */
fun BlockStateProviderScope.copyPropertiesProvider(source: BlockStateProvider) = CopyPropertiesProvider(source)
