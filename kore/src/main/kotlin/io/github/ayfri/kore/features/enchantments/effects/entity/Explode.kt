package io.github.ayfri.kore.features.enchantments.effects.entity

import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.arguments.types.resources.SoundArgument
import io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.types.ParticleType
import io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.types.particleType
import io.github.ayfri.kore.features.enchantments.values.LevelBased
import io.github.ayfri.kore.features.enchantments.values.constantLevelBased
import io.github.ayfri.kore.generated.arguments.types.DamageTypeArgument
import io.github.ayfri.kore.generated.arguments.types.ParticleTypeArgument
import io.github.ayfri.kore.serializers.InlinableList
import io.github.ayfri.kore.serializers.LowercaseSerializer
import io.github.ayfri.kore.serializers.TripleAsArray
import kotlinx.serialization.Serializable

@Serializable(with = BlockInteraction.Companion.BlockInteractionSerializer::class)
enum class BlockInteraction {
	BLOCK,
	MOB,
	NONE,
	TNT,
	TRIGGER,
	;

	companion object {
		data object BlockInteractionSerializer : LowercaseSerializer<BlockInteraction>(entries)
	}
}

@Serializable
data class ExplodeBlockEffect(
	var weight: Int,
	var particle: ParticleType,
	var scaling: Float? = null,
	var speed: Float? = null,
)

@Serializable
data class Explode(
	var attributeToUser: Boolean,
	var blockEffects: List<ExplodeBlockEffect>? = null,
	var blockInteraction: BlockInteraction,
	var createFire: Boolean,
	var damageType: DamageTypeArgument? = null,
	var immuneBlocks: InlinableList<BlockOrTagArgument>? = null,
	var knockbackMultiplier: LevelBased? = null,
	var largeParticle: ParticleTypeArgument,
	var offset: TripleAsArray<Int, Int, Int>? = null,
	var radius: LevelBased = constantLevelBased(0),
	var smallParticle: ParticleTypeArgument,
	var sound: SoundArgument,
) : EntityEffect()

fun Explode.blockEffects(block: MutableList<ExplodeBlockEffect>.() -> Unit) {
	blockEffects = buildList(block)
}

fun MutableList<ExplodeBlockEffect>.effect(weight: Int, particle: ParticleType, scaling: Float? = null, speed: Float? = null) {
	add(ExplodeBlockEffect(weight, particle, scaling, speed))
}

fun MutableList<ExplodeBlockEffect>.effect(
	weight: Int,
	particle: ParticleTypeArgument,
	scaling: Float? = null,
	speed: Float? = null,
) {
	add(ExplodeBlockEffect(weight, particleType(particle), scaling, speed))
}

fun Explode.damageType(value: DamageTypeArgument) {
	damageType = value
}

fun Explode.immuneBlocks(vararg blocks: BlockOrTagArgument) {
	immuneBlocks = blocks.toList()
}

fun Explode.immuneBlocks(block: MutableList<BlockOrTagArgument>.() -> Unit) {
	immuneBlocks = buildList(block)
}

fun Explode.knockbackMultiplier(value: Int) {
	knockbackMultiplier = constantLevelBased(value)
}

fun Explode.offset(x: Int, y: Int, z: Int) {
	offset = Triple(x, y, z)
}

fun Explode.radius(value: Int) {
	radius = constantLevelBased(value)
}
