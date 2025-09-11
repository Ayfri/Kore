package io.github.ayfri.kore.arguments.actions

import io.github.ayfri.kore.utils.nbt
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.NbtCompoundBuilder
import net.benwoodworth.knbt.NbtTag

@Serializable
data class Custom(
	var id: String,
	var payload: NbtTag? = null,
) : Action()

fun ActionWrapper.custom(id: String, payload: NbtTag? = null) = apply { action = Custom(id, payload) }
fun ActionWrapper.custom(id: String, payload: String? = null) = apply { action = Custom(id, payload?.nbt) }
fun ActionWrapper.custom(id: String, payload: NbtCompoundBuilder.() -> Unit) = apply { action = Custom(id, nbt(payload)) }
