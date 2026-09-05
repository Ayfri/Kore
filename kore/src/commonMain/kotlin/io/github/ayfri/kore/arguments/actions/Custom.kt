package io.github.ayfri.kore.arguments.actions

import io.github.ayfri.kore.utils.nbt
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.NbtCompoundBuilder
import net.benwoodworth.knbt.NbtTag

/** Sends a custom packet with [id] and optional [payload] to the dedicated server. Ignored by vanilla servers. */
@Serializable
data class Custom(
	/** Namespaced packet identifier. */
	var id: String,
	/** Optional NBT payload attached to the packet. */
	var payload: NbtTag? = null,
) : Action(), ClickEvent, DialogAction

/** Sends a custom packet to the dedicated server, not useful for datapacks on vanilla servers. */
fun ActionWrapper<*>.custom(id: String, payload: NbtTag? = null) = apply { action = Custom(id, payload) }

/** Sends a custom packet to the dedicated server, not useful for datapacks on vanilla servers. */
fun ActionWrapper<*>.custom(id: String, payload: String? = null) = apply { action = Custom(id, payload?.nbt) }

/** Sends a custom packet to the dedicated server, not useful for datapacks on vanilla servers. */
fun ActionWrapper<*>.custom(id: String, payload: NbtCompoundBuilder.() -> Unit) = apply { action = Custom(id, nbt(payload)) }
