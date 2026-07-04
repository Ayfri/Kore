package io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.types

import io.github.ayfri.kore.arguments.components.Components
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.data.item.ItemStack
import io.github.ayfri.kore.data.item.builders.itemStack
import io.github.ayfri.kore.generated.arguments.types.ParticleTypeArgument
import kotlinx.serialization.Serializable

@Serializable
data class ItemParticleType(
	override var type: ParticleTypeArgument,
	var item: ItemStack,
) : ParticleType()

fun itemParticleType(type: ParticleTypeArgument, item: ItemStack) = ItemParticleType(type, item)
fun itemParticleType(type: ParticleTypeArgument, item: ItemArgument, count: Short? = null, init: (Components.() -> Unit)? = null) =
	ItemParticleType(type, itemStack(item, count, init))
