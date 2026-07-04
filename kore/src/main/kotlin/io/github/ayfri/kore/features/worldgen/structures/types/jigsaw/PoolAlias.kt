package io.github.ayfri.kore.features.worldgen.structures.types.jigsaw

import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@GeneratedSealedSerializer
@Serializable(with = PoolAlias.Companion.PoolAliasSerializer::class)
sealed class PoolAlias {
	companion object {
		@OptIn(InternalSerializationApi::class)
		data object PoolAliasSerializer : NamespacedPolymorphicSerializer<PoolAlias>(poolAliasSealedSerializer())
	}
}
