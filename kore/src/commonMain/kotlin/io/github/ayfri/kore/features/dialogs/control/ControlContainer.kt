package io.github.ayfri.kore.features.dialogs.control

import kotlinx.serialization.Serializable

@Serializable
data class ControlContainer(var controls: List<DialogControl> = emptyList())
