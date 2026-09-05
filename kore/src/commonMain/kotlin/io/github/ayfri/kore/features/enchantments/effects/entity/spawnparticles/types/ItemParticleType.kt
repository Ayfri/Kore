package io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.types

import io.github.ayfri.kore.arguments.components.Components
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.data.item.ItemStack
import io.github.ayfri.kore.data.item.builders.itemStack
import io.github.ayfri.kore.generated.arguments.types.ParticleTypeArgument
import kotlinx.serialization.Serializable

/**
 * A particle textured after [item], such as `item`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Particle#item
 *
 * @property type The id of the particle.
 * @property item The item stack the particle takes its texture from.
 */
@Serializable
data class ItemParticleType(
	override var type: ParticleTypeArgument,
	var item: ItemStack,
) : ParticleType()

/** Creates the options of an item-textured particle. */
fun ParticleTypeScope.itemParticleType(type: ParticleTypeArgument, item: ItemStack) = ItemParticleType(type, item)
/** Creates the options of an item-textured particle. */
fun ParticleTypeScope.itemParticleType(
	type: ParticleTypeArgument,
	item: ItemArgument,
	count: Short? = null,
	init: (Components.() -> Unit)? = null,
) =
	ItemParticleType(type, itemStack(item, count, init))
