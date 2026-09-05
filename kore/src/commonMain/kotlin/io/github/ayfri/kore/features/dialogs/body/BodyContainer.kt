package io.github.ayfri.kore.features.dialogs.body

import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class BodyContainer(var bodies: InlinableList<DialogBody> = emptyList())
