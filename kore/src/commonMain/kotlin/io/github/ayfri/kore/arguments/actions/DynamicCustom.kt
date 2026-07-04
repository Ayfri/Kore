package io.github.ayfri.kore.arguments.actions

import io.github.ayfri.kore.serializers.NbtAsJsonSerializer
import io.github.ayfri.kore.utils.nbt
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.NbtCompoundBuilder
import net.benwoodworth.knbt.NbtTag

/**
 * Sends a dynamically constructed custom packet to the dedicated server. Macro names in [id] or [additions] are
 * substituted with matching dialog input values; undefined macros are replaced with an empty string.
 * Ignored by vanilla servers.
 */
@SerialName("dynamic/custom")
@Serializable
data class DynamicCustom(
	/** Namespaced packet identifier. May contain macro placeholders. */
	var id: String,
	/** Optional NBT additions merged into the packet payload. May contain macro placeholders. */
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
