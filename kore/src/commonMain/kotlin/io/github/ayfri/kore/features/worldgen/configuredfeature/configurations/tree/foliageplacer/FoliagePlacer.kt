package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.foliageplacer

import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProviderScope
import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

/**
 * Shapes the leaves of a `tree` feature, each subtype being the leaf layout of one or several vanilla tree species.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 *
 * @property radius Horizontal radius of the leaves, `0` to `16`.
 * @property offset Vertical offset from the top of the trunk to the foliage, `0` to `16`.
 */
@GeneratedSealedSerializer
@Serializable(with = FoliagePlacer.Companion.FoliagePlacerSerializer::class)
sealed class FoliagePlacer : IntProviderScope {
	abstract var radius: IntProvider
	abstract var offset: IntProvider

	companion object {
		data object FoliagePlacerSerializer :
			NamespacedPolymorphicSerializer<FoliagePlacer>(foliagePlacerSealedSerializer())
	}
}
