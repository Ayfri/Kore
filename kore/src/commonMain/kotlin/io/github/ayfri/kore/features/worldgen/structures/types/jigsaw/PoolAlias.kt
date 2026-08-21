package io.github.ayfri.kore.features.worldgen.structures.types.jigsaw

import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/**
 * A rewiring rule redirecting a template pool an
 * [io.github.ayfri.kore.features.worldgen.structures.types.Jigsaw] structure references to another one.
 *
 * Aliases are resolved once per structure instance, which is how a single jigsaw structure produces differently themed
 * variants without duplicating every pool it is built from. Declare them through
 * [io.github.ayfri.kore.features.worldgen.structures.types.poolAliases].
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/structures
 * Minecraft Wiki: https://minecraft.wiki/w/Structure_definition
 */
@GeneratedSealedSerializer
@Serializable(with = PoolAlias.Companion.PoolAliasSerializer::class)
sealed class PoolAlias {
	companion object {
		@OptIn(InternalSerializationApi::class)
		data object PoolAliasSerializer : NamespacedPolymorphicSerializer<PoolAlias>(poolAliasSealedSerializer())
	}
}
