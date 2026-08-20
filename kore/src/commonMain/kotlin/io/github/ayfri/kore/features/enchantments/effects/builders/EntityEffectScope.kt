package io.github.ayfri.kore.features.enchantments.effects.builders

import io.github.ayfri.kore.arguments.maths.Vec3f
import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.data.sound.SoundEvent
import io.github.ayfri.kore.features.enchantments.effects.entity.*
import io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.ParticlePosition
import io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.ParticlePositionType
import io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.types.ParticleType
import io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.types.ParticleTypeScope
import io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.types.particleType
import io.github.ayfri.kore.features.enchantments.values.Constant
import io.github.ayfri.kore.features.enchantments.values.LevelBased
import io.github.ayfri.kore.features.enchantments.values.LevelBasedScope
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.generated.arguments.EntityTypeOrTagArgument
import io.github.ayfri.kore.generated.arguments.MobEffectOrTagArgument
import io.github.ayfri.kore.generated.arguments.types.DamageTypeArgument
import io.github.ayfri.kore.generated.arguments.types.ParticleTypeArgument
import io.github.ayfri.kore.generated.arguments.types.SoundEventArgument

/**
 * Receiver of the [EntityEffect] builders, implemented by every component that runs effects on an entity.
 *
 * A single set of builders serves `hit_block`, `tick`, `projectile_spawned`, `location_changed`,
 * `post_piercing_attack`, `post_attack` and the nested `all_of` blocks: each scope only decides where [addEffect]
 * puts the effect it is handed.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#Entity_effects
 */
interface EntityEffectScope : LevelBasedScope, ParticleTypeScope {
	/** Appends [effect] to the component being built, lifting its requirements next to it where the format allows. */
	fun addEffect(effect: EntityEffect)
}

/**
 * Collects the effects of an `all_of` block, running them in order.
 *
 * `requirements { }` is only honored on the outermost `all_of` of a component, since the nested ones have nowhere to
 * put it in the JSON.
 */
class EntityEffectAllOfScope internal constructor(internal val allOf: AllOf = AllOf()) : EntityEffectScope {
	override fun addEffect(effect: EntityEffect) {
		allOf.effects += effect
	}
}

/** Sets the conditions the whole `all_of` block runs under. */
fun EntityEffectAllOfScope.requirements(block: Predicate.() -> Unit = {}) {
	allOf.requirements = Predicate().apply(block).predicateConditions
}

/**
 * Appends an `all_of` effect running every effect built in [block] in order.
 *
 * ```kotlin
 * hitBlock {
 *     allOf {
 *         requirements { weatherCheck(raining = true) }
 *         ignite(5)
 *         playSound(SoundEvents.Entity.Generic.EXTINGUISH_FIRE)
 *     }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#all_of
 */
fun EntityEffectScope.allOf(block: EntityEffectAllOfScope.() -> Unit = {}) =
	addEffect(EntityEffectAllOfScope().apply(block).allOf)

/**
 * Appends an `apply_exhaustion` effect adding [amount] hunger exhaustion to the affected player.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#apply_exhaustion
 */
fun EntityEffectScope.applyExhaustion(amount: LevelBased = Constant(0f), block: ApplyExhaustion.() -> Unit = {}) =
	addEffect(ApplyExhaustion(amount).apply(block))

/** Appends an `apply_exhaustion` effect adding a constant [amount] of hunger exhaustion to the affected player. */
fun EntityEffectScope.applyExhaustion(amount: Int, block: ApplyExhaustion.() -> Unit = {}) =
	addEffect(ApplyExhaustion(Constant(amount.toFloat())).apply(block))

/**
 * Appends an `apply_impulse` effect pushing the affected entity along [direction] with a strength of [magnitude],
 * after scaling its current motion by [coordinateScale].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#apply_impulse
 */
fun EntityEffectScope.applyImpulse(
	coordinateScale: Vec3f,
	direction: Vec3f,
	magnitude: LevelBased,
	block: ApplyImpulse.() -> Unit = {},
) = addEffect(ApplyImpulse(coordinateScale, direction, magnitude).apply(block))

/**
 * Appends an `apply_mob_effect` effect giving the affected entity one of the mob effects picked in [block].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#apply_mob_effect
 */
fun EntityEffectScope.applyMobEffect(block: ApplyMobEffect.() -> Unit = {}) =
	addEffect(ApplyMobEffect().apply(block))

/**
 * Appends an `apply_mob_effect` effect giving [effect] to the affected entity.
 *
 * ```kotlin
 * applyMobEffect(Effects.SLOWNESS) {
 *     duration(100)
 *     amplifier(1)
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#apply_mob_effect
 */
fun EntityEffectScope.applyMobEffect(effect: MobEffectOrTagArgument, block: ApplyMobEffect.() -> Unit = {}) =
	addEffect(ApplyMobEffect(listOf(effect)).apply(block))

/**
 * Appends a `damage_entity` effect dealing [damageType] damage to the affected entity.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#damage_entity
 */
fun EntityEffectScope.damageEntity(damageType: DamageTypeArgument, block: DamageEntity.() -> Unit = {}) =
	addEffect(DamageEntity(damageType).apply(block))

/**
 * Appends a `damage_entity` effect dealing between [minDamage] and [maxDamage] half-hearts of [damageType] damage to
 * the affected entity.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#damage_entity
 */
fun EntityEffectScope.damageEntity(
	damageType: DamageTypeArgument,
	minDamage: Int,
	maxDamage: Int,
	block: DamageEntity.() -> Unit = {},
) = addEffect(DamageEntity(damageType, Constant(minDamage.toFloat()), Constant(maxDamage.toFloat())).apply(block))

/**
 * Appends a `damage_item` effect removing durability from the enchanted item.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#damage_item
 */
fun EntityEffectScope.damageItem(block: DamageItem.() -> Unit = {}) = addEffect(DamageItem().apply(block))

/** Appends a `damage_item` effect removing a constant [amount] of durability from the enchanted item. */
fun EntityEffectScope.damageItem(amount: Int, block: DamageItem.() -> Unit = {}) =
	addEffect(DamageItem(Constant(amount.toFloat())).apply(block))

/**
 * Appends an `explode` effect detonating an explosion at the position of the affected entity.
 *
 * ```kotlin
 * explode(
 *     smallParticle = particleType(Particles.GUST_EMITTER_SMALL),
 *     largeParticle = particleType(Particles.GUST_EMITTER_LARGE),
 *     sound = SoundEvents.Entity.WindCharge.WIND_BURST,
 * ) {
 *     blockInteraction = BlockInteraction.TRIGGER
 *     radius(3.5f)
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#explode
 */
fun EntityEffectScope.explode(
	smallParticle: ParticleType,
	largeParticle: ParticleType,
	sound: SoundEventArgument,
	block: Explode.() -> Unit = {},
) = addEffect(Explode(largeParticle, smallParticle, sound).apply(block))

/** Appends an `explode` effect using the default options of [smallParticle] and [largeParticle]. */
fun EntityEffectScope.explode(
	smallParticle: ParticleTypeArgument,
	largeParticle: ParticleTypeArgument,
	sound: SoundEventArgument,
	block: Explode.() -> Unit = {},
) = addEffect(Explode(particleType(largeParticle), particleType(smallParticle), sound).apply(block))

/**
 * Appends an `ignite` effect setting the affected entity on fire for [duration] seconds.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#ignite
 */
fun EntityEffectScope.ignite(duration: LevelBased = Constant(0f), block: Ignite.() -> Unit = {}) =
	addEffect(Ignite(duration).apply(block))

/** Appends an `ignite` effect setting the affected entity on fire for a constant [duration] in seconds. */
fun EntityEffectScope.ignite(duration: Int, block: Ignite.() -> Unit = {}) =
	addEffect(Ignite(Constant(duration.toFloat())).apply(block))

/**
 * Appends a `play_sound` effect playing [sound] at the position of the affected entity, audible up to [range] blocks
 * away.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#play_sound
 */
fun EntityEffectScope.playSound(sound: SoundEventArgument, range: Float? = null, block: PlaySound.() -> Unit = {}) =
	addEffect(PlaySound(listOf(SoundEvent(sound, range))).apply(block))

/**
 * Appends a `replace_block` effect replacing the block at the target position by [ReplaceBlock.blockState].
 *
 * The block state provider and block predicate builders are scoped to [block].
 *
 * ```kotlin
 * replaceBlock {
 *     blockState = simpleStateProvider(Blocks.WATER)
 *     predicate { matchingBlocks(Blocks.LAVA) }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#replace_block
 */
fun EntityEffectScope.replaceBlock(block: ReplaceBlock.() -> Unit = {}) = addEffect(ReplaceBlock().apply(block))

/**
 * Appends a `replace_disk` effect replacing a disk of blocks around the target position by [ReplaceDisk.blockState],
 * the way Frost Walker freezes the water it steps on.
 *
 * The block state provider and block predicate builders are scoped to [block].
 *
 * ```kotlin
 * replaceDisk {
 *     blockState = simpleStateProvider(Blocks.FROSTED_ICE)
 *     radius(3)
 *     predicate { matchingBlocks(Blocks.WATER) }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#replace_disk
 */
fun EntityEffectScope.replaceDisk(block: ReplaceDisk.() -> Unit = {}) = addEffect(ReplaceDisk().apply(block))

/**
 * Appends a `run_function` effect running [function] at the position of the affected entity.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#run_function
 */
fun EntityEffectScope.runFunction(function: FunctionArgument, block: RunFunction.() -> Unit = {}) =
	addEffect(RunFunction(function).apply(block))

/**
 * Appends a `set_block_properties` effect overriding block state properties at the target position.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#set_block_properties
 */
fun EntityEffectScope.setBlockProperties(block: SetBlockProperties.() -> Unit = {}) =
	addEffect(SetBlockProperties().apply(block))

/**
 * Appends a `spawn_particles` effect spawning [particle] around the affected entity.
 *
 * Offsets and scales are set on the positions inside [block], with `horizontalPosition { }` and
 * `verticalPosition { }`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#spawn_particles
 */
fun EntityEffectScope.spawnParticles(
	particle: ParticleType,
	horizontalPositionType: ParticlePositionType,
	verticalPositionType: ParticlePositionType,
	block: SpawnParticles.() -> Unit = {},
) = addEffect(
	SpawnParticles(
		particle,
		horizontalPosition = ParticlePosition(horizontalPositionType),
		verticalPosition = ParticlePosition(verticalPositionType),
	).apply(block)
)

/** Appends a `spawn_particles` effect spawning [particle] with its default options around the affected entity. */
fun EntityEffectScope.spawnParticles(
	particle: ParticleTypeArgument,
	horizontalPositionType: ParticlePositionType,
	verticalPositionType: ParticlePositionType,
	block: SpawnParticles.() -> Unit = {},
) = spawnParticles(particleType(particle), horizontalPositionType, verticalPositionType, block)

/**
 * Appends a `summon_entity` effect summoning one of the entity types picked in [block].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#summon_entity
 */
fun EntityEffectScope.summonEntity(block: SummonEntity.() -> Unit = {}) = addEffect(SummonEntity().apply(block))

/**
 * Appends a `summon_entity` effect summoning one of the [entityType] entities at the position of the affected entity.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#summon_entity
 */
fun EntityEffectScope.summonEntity(
	vararg entityType: EntityTypeOrTagArgument,
	joinTeam: Boolean? = null,
	block: SummonEntity.() -> Unit = {},
) = addEffect(SummonEntity(entityType.toList(), joinTeam).apply(block))
