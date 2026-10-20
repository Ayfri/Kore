package io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider

import kotlinx.serialization.Serializable

/**
 * Takes the block state given by [sourceBlockStateProvider] and copies onto it the properties it shares with the block
 * already at the position.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#copy_properties_provider
 *
 * @property sourceBlockStateProvider The provider giving the block state the properties are copied onto.
 */
@Serializable
data class CopyPropertiesProvider(
	var sourceBlockStateProvider: BlockStateProvider = SimpleStateProvider(),
) : BlockStateProvider()

/**
 * Creates a `copy_properties_provider`, copying the properties the block already at the position shares with the
 * state given by [sourceBlockStateProvider].
 *
 * ```kotlin
 * simpleBlock("waxed_copper") {
 *     toPlace = copyPropertiesProvider(simpleStateProvider(Blocks.WAXED_COPPER_BLOCK))
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#copy_properties_provider
 */
fun BlockStateProviderScope.copyPropertiesProvider(sourceBlockStateProvider: BlockStateProvider) =
	CopyPropertiesProvider(sourceBlockStateProvider)
