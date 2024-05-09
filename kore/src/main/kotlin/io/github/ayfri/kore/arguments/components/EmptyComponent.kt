package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.arguments.components.types.CustomComponent
import io.github.ayfri.kore.utils.nbt
import kotlinx.serialization.Serializable

@Serializable(with = CustomComponent.Companion.CustomComponentSerializer::class)
class EmptyComponent : CustomComponent(nbt {})
