package io.github.ayfri.kore.features.enchantments.effects.entity

import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.types.ParticleType
import io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.types.ParticleTypeScope
import io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.types.particleType
import io.github.ayfri.kore.features.enchantments.values.Constant
import io.github.ayfri.kore.features.enchantments.values.LevelBased
import io.github.ayfri.kore.features.enchantments.values.LevelBasedScope
import io.github.ayfri.kore.features.enchantments.values.constantLevelBased
import io.github.ayfri.kore.generated.arguments.types.DamageTypeArgument
import io.github.ayfri.kore.generated.arguments.types.ParticleTypeArgument
import io.github.ayfri.kore.generated.arguments.types.SoundEventArgument
import io.github.ayfri.kore.serializers.InlinableList
import io.github.ayfri.kore.serializers.LowercaseSerializer
import io.github.ayfri.kore.serializers.TripleAsArray
import kotlinx.serialization.Serializable

/**
 * How an [Explode] effect treats the blocks caught in its radius.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Explosion
 */
@Serializable(with = BlockInteraction.Companion.BlockInteractionSerializer::class)
enum class BlockInteraction {
	/** Blocks are destroyed and drop as if mined, like a bed exploding. */
	BLOCK,

	/** Blocks are destroyed and drop as if mined by a mob, honoring the mob griefing rule. */
	MOB,

	/** Blocks are left untouched. */
	NONE,

	/** Blocks are destroyed and drop with the usual TNT drop chance. */
	TNT,

	/** Blocks are left standing but triggered, like a wind charge pressing buttons. */
	TRIGGER,
	;

	companion object {
		data object BlockInteractionSerializer : LowercaseSerializer<BlockInteraction>(entries)
	}
}

/**
 * One of the particles an [Explode] effect throws out of the blocks it destroys, picked against the [weight] of the
 * other entries.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#explode
 *
 * @property weight The relative chance of this particle being picked.
 * @property particle The particle spawned.
 * @property scaling The size of the particle, left to the particle default when `null`.
 * @property speed The speed the particle flies away at, left to the particle default when `null`.
 */
@Serializable
data class ExplodeBlockParticle(
	var weight: Int,
	var particle: ParticleType,
	var scaling: Float? = null,
	var speed: Float? = null,
) : ParticleTypeScope

/** Receiver of the [ExplodeBlockParticle] builders, collecting the particles thrown by an [Explode] effect. */
class ExplodeBlockParticlesScope internal constructor() : ParticleTypeScope {
	internal val particles = mutableListOf<ExplodeBlockParticle>()
}

/** Appends a particle thrown by the explosion, picked against the [weight] of the other entries. */
fun ExplodeBlockParticlesScope.particle(
	weight: Int,
	particle: ParticleType,
	scaling: Float? = null,
	speed: Float? = null,
) {
	particles += ExplodeBlockParticle(weight, particle, scaling, speed)
}

/** Appends a particle thrown by the explosion, picked against the [weight] of the other entries. */
fun ExplodeBlockParticlesScope.particle(
	weight: Int,
	particle: ParticleTypeArgument,
	scaling: Float? = null,
	speed: Float? = null,
) {
	particles += ExplodeBlockParticle(weight, particleType(particle), scaling, speed)
}

/**
 * Detonates an explosion of [radius] blocks at the position of the affected entity, the way the Wind Burst
 * enchantment does when a mace lands.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#explode
 *
 * @property attributeToUser Whether the explosion is credited to the enchanted entity, making it responsible for the kills.
 * @property blockInteraction How the blocks caught in the explosion are treated.
 * @property blockParticles The particles thrown out of the destroyed blocks, the default ones when `null`.
 * @property createFire Whether the explosion sets the blocks it touches on fire.
 * @property damageType The damage type the explosion is attributed to, no damage dealt when `null`.
 * @property immuneBlocks The blocks and block tags the explosion cannot destroy, none when `null`.
 * @property knockbackMultiplier The factor applied to the knockback dealt, `1` when `null`.
 * @property largeParticle The particle spawned at the center of a large explosion.
 * @property offset The `[X, Y, Z]` offset applied to the center of the explosion, `[0, 0, 0]` when `null`.
 * @property radius The radius of the explosion in blocks.
 * @property smallParticle The particle spawned at the center of a small explosion.
 * @property sound The sound event played when the explosion goes off.
 */
@Serializable
data class Explode(
	var largeParticle: ParticleType,
	var smallParticle: ParticleType,
	var sound: SoundEventArgument,
	var attributeToUser: Boolean = false,
	var blockInteraction: BlockInteraction = BlockInteraction.NONE,
	var blockParticles: List<ExplodeBlockParticle>? = null,
	var createFire: Boolean = false,
	var damageType: DamageTypeArgument? = null,
	var immuneBlocks: InlinableList<BlockOrTagArgument>? = null,
	var knockbackMultiplier: LevelBased? = null,
	var offset: TripleAsArray<Float, Float, Float>? = null,
	var radius: LevelBased = Constant(0f),
) : EntityEffect(), LevelBasedScope, ParticleTypeScope

/** Sets [Explode.blockParticles] to the particles collected in [block], thrown out of the destroyed blocks. */
fun Explode.blockParticles(block: ExplodeBlockParticlesScope.() -> Unit) {
	blockParticles = ExplodeBlockParticlesScope().apply(block).particles
}

/** Sets [Explode.immuneBlocks], the blocks and block tags the explosion cannot destroy. */
fun Explode.immuneBlocks(vararg blocks: BlockOrTagArgument) {
	immuneBlocks = blocks.toList()
}

/** Sets [Explode.knockbackMultiplier] to a constant [value], the factor applied to the knockback dealt. */
fun Explode.knockbackMultiplier(value: Float) {
	knockbackMultiplier = constantLevelBased(value)
}

/** Sets [Explode.knockbackMultiplier] to a constant [value], the factor applied to the knockback dealt. */
fun Explode.knockbackMultiplier(value: Int) {
	knockbackMultiplier = constantLevelBased(value)
}

/** Centers the explosion on the position offset by [x], [y] and [z] from the affected entity. */
fun Explode.offset(x: Float, y: Float, z: Float) {
	offset = Triple(x, y, z)
}

/** Sets [Explode.radius] to a constant [value] in blocks, whatever the enchantment level is. */
fun Explode.radius(value: Float) {
	radius = constantLevelBased(value)
}

/** Sets [Explode.radius] to a constant [value] in blocks, whatever the enchantment level is. */
fun Explode.radius(value: Int) {
	radius = constantLevelBased(value)
}
