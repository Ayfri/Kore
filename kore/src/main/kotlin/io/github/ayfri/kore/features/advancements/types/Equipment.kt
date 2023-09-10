package io.github.ayfri.kore.features.advancements.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Equipment(
	@SerialName("mainhand")
	var mainHand: ItemStack? = null,
	@SerialName("offhand")
	var offHand: ItemStack? = null,
	var head: ItemStack? = null,
	var chest: ItemStack? = null,
	var legs: ItemStack? = null,
	var feet: ItemStack? = null,
)

fun equipment(init: Equipment.() -> Unit = {}) = Equipment().apply(init)
