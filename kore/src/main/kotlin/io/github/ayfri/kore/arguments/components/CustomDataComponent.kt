package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.generated.ComponentTypes
import io.github.ayfri.kore.utils.nbt
import net.benwoodworth.knbt.NbtCompoundBuilder
import net.benwoodworth.knbt.NbtTag
import kotlinx.serialization.Serializable

@Serializable
data class CustomDataComponent(override val nbt: NbtTag) : CustomComponent()

fun Components.customData(nbt: NbtTag) = apply { this[ComponentTypes.CUSTOM_DATA] = CustomDataComponent(nbt) }
fun Components.customData(block: NbtCompoundBuilder.() -> Unit) = customData(nbt(block))
