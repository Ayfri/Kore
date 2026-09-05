package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations.tree.treedecorator

import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@GeneratedSealedSerializer
@Serializable(with = TreeDecorator.Companion.TreeDecoratorSerializer::class)
sealed class TreeDecorator {
	companion object {
		@OptIn(InternalSerializationApi::class)
		data object TreeDecoratorSerializer :
			NamespacedPolymorphicSerializer<TreeDecorator>(treeDecoratorSealedSerializer())
	}
}
