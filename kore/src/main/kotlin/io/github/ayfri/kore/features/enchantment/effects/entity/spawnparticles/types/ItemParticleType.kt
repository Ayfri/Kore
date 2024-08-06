package io.github.ayfri.kore.features.enchantment.effects.entity.spawnparticles.types

import io.github.ayfri.kore.arguments.components.Components
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.arguments.types.resources.ParticleArgument
import io.github.ayfri.kore.data.item.ItemStack
import io.github.ayfri.kore.data.item.builders.itemStack
import kotlinx.serialization.Serializable

@Serializable
data class ItemParticleType(
	override var type: ParticleArgument,
	var item: ItemStack,
) : ParticleType()

fun itemParticleType(type: ParticleArgument, item: ItemStack) = ItemParticleType(type, item)
fun itemParticleType(type: ParticleArgument, item: ItemArgument, count: Short? = null, init: (Components.() -> Unit)? = null) =
	ItemParticleType(type, itemStack(item, count, init))
