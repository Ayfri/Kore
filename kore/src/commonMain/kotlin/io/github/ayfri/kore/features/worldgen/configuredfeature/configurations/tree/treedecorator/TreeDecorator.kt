package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.treedecorator

import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

/**
 * Adds blocks to various parts of a `tree` feature once the trunk and the foliage are placed, such as beehives, vines
 * or cocoa beans.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#tree
 */
@GeneratedSealedSerializer
@Serializable(with = TreeDecorator.Companion.TreeDecoratorSerializer::class)
sealed class TreeDecorator {
	companion object {
		data object TreeDecoratorSerializer :
			NamespacedPolymorphicSerializer<TreeDecorator>(treeDecoratorSealedSerializer())
	}
}
