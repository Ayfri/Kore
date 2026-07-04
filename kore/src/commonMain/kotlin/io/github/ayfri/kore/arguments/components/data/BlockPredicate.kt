package io.github.ayfri.kore.arguments.components.data

import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.NbtCompound

@Serializable
data class BlockPredicate(
	val blocks: InlinableList<BlockOrTagArgument>,
	val nbt: NbtCompound? = null,
	val state: MutableMap<String, String>? = null,
)
