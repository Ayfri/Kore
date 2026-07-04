package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.arguments.components.item.CustomComponent
import io.github.ayfri.kore.utils.nbt
import kotlinx.serialization.Serializable

@Serializable(with = CustomComponent.Companion.CustomComponentSerializer::class)
data object EmptyComponent : CustomComponent(nbt {})
