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

/** Sends a dynamically built custom packet to the dedicated server, not useful for datapacks on vanilla servers,
 * you can use macros with the same names as the inputs for a custom payload,
 * undefined macros will just be replaced with an empty string. */
fun DialogActionContainer.dynamicCustom(id: String, additions: NbtTag? = null) = apply { action = DynamicCustom(id, additions) }

/** Sends a dynamically built custom packet to the dedicated server, not useful for datapacks on vanilla servers,
 * you can use macros with the same names as the inputs for a custom payload,
 * undefined macros will just be replaced with an empty string. */
fun DialogActionContainer.dynamicCustom(id: String, additions: String) = apply { action = DynamicCustom(id, additions.nbt) }

/** Sends a dynamically built custom packet to the dedicated server, not useful for datapacks on vanilla servers,
 * you can use macros with the same names as the inputs for a custom payload,
 * undefined macros will just be replaced with an empty string. */
fun DialogActionContainer.dynamicCustom(id: String, additions: NbtCompoundBuilder.() -> Unit) = apply {
	action = DynamicCustom(id, nbt(additions))
}
