package io.github.ayfri.kore.features.worldgen.processorlist.types.rule.blockentitymodifier

import io.github.ayfri.kore.serializers.NbtAsJsonSerializer
import io.github.ayfri.kore.utils.nbt
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.NbtCompoundBuilder
import kotlinx.serialization.Serializable

@Serializable
data class AppendStatic(
	@Serializable(with = NbtAsJsonSerializer::class)
	var data: NbtCompound = nbt {},
) : BlockEntityModifier()

fun appendStatic(data: NbtCompound = nbt {}) = AppendStatic(data)
fun appendStatic(block: NbtCompoundBuilder.() -> Unit) = AppendStatic(nbt(block))
