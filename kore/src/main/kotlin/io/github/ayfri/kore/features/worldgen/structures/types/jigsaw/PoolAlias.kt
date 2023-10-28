package io.github.ayfri.kore.features.worldgen.structures.types.jigsaw

import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = PoolAlias.Companion.PoolAliasSerializer::class)
sealed class PoolAlias {
	companion object {
		data object PoolAliasSerializer : NamespacedPolymorphicSerializer<PoolAlias>(PoolAlias::class)
	}
}
