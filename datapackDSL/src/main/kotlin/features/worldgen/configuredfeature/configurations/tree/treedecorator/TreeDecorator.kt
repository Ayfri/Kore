package features.worldgen.configuredfeature.configurations.tree.treedecorator

import serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = TreeDecorator.Companion.TreeDecoratorSerializer::class)
sealed class TreeDecorator {
	companion object {
		data object TreeDecoratorSerializer : NamespacedPolymorphicSerializer<TreeDecorator>(TreeDecorator::class)
	}
}
