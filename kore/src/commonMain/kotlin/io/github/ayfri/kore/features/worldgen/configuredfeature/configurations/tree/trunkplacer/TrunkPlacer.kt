package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.trunkplacer

import io.github.ayfri.kore.features.worldgen.intproviders.IntProviderScope
import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

/**
 * Shapes the trunk of a `tree` feature, each subtype being the trunk layout of one or several vanilla tree species.
 *
 * The trunk height is [baseHeight] plus a random value from `0` to [heightRandA] plus another one from `0` to [heightRandB].
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 *
 * @property baseHeight Base height of the trunk, `0` to `32`.
 * @property heightRandA Upper bound of the first random height added to [baseHeight], `0` to `24`.
 * @property heightRandB Upper bound of the second random height added to [baseHeight], `0` to `24`.
 */
@GeneratedSealedSerializer
@Serializable(with = TrunkPlacer.Companion.TrunkPlacerSerializer::class)
sealed class TrunkPlacer : IntProviderScope {
	abstract var baseHeight: Int
	abstract var heightRandA: Int
	abstract var heightRandB: Int

	companion object {
		data object TrunkPlacerSerializer : NamespacedPolymorphicSerializer<TrunkPlacer>(trunkPlacerSealedSerializer())
	}
}
