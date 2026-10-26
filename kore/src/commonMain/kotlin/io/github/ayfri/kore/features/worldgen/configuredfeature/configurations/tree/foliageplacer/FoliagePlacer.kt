package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.foliageplacer

import io.github.ayfri.kore.features.worldgen.intproviders.ConstantIntProvider
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

/**
 * Sets [FoliagePlacer.offset] to [value], the vertical offset from the top of the trunk to the foliage.
 *
 * ```kotlin
 * blobFoliagePlacer(radius = 2, height = 3) { offset(uniform(0, 1)) }
 * ```
 */
fun FoliagePlacer.offset(value: IntProvider) {
	offset = value
}

/** Sets [FoliagePlacer.offset] to the constant [value]. */
fun FoliagePlacer.offset(value: Int) = offset(ConstantIntProvider(value))

/**
 * Sets [FoliagePlacer.radius] to [value], the horizontal radius of the leaves.
 *
 * ```kotlin
 * blobFoliagePlacer(height = 3) { radius(uniform(2, 3)) }
 * ```
 */
fun FoliagePlacer.radius(value: IntProvider) {
	radius = value
}

/** Sets [FoliagePlacer.radius] to the constant [value]. */
fun FoliagePlacer.radius(value: Int) = radius(ConstantIntProvider(value))
