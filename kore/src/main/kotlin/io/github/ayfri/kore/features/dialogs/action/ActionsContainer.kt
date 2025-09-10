package io.github.ayfri.kore.features.dialogs.action

import kotlinx.serialization.Serializable

@Serializable
data class ActionsContainer<T : DialogButton>(var actions: List<T> = emptyList())
