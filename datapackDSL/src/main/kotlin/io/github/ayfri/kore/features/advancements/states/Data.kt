package io.github.ayfri.kore.features.advancements.states

import kotlinx.serialization.Serializable

@Serializable
sealed interface StateData

@JvmInline
@Serializable
value class StateBoolean(
	val value: Boolean,
) : StateData

@JvmInline
@Serializable
value class StateInt(
	val value: Int,
) : StateData

@JvmInline
@Serializable
value class StateString(
	val value: String,
) : StateData
