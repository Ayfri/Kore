package io.github.ayfri.kore.arguments.actions

import io.github.ayfri.kore.serializers.NbtAsJsonSerializer
import io.github.ayfri.kore.utils.nbt
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.NbtCompoundBuilder
import net.benwoodworth.knbt.NbtTag

@SerialName("dynamic/custom")
@Serializable
data class DynamicCustom(
	var id: String,
	@Serializable(with = NbtAsJsonSerializer::class)
	var additions: NbtTag? = null,
) : Action(), DialogAction

fun DialogActionContainer.dynamicCustom(id: String, additions: NbtTag? = null) = apply { action = DynamicCustom(id, additions) }
fun DialogActionContainer.dynamicCustom(id: String, additions: String) = apply { action = DynamicCustom(id, additions?.nbt) }
fun DialogActionContainer.dynamicCustom(id: String, additions: NbtCompoundBuilder.() -> Unit) = apply {
	action = DynamicCustom(id, nbt(additions))
}
