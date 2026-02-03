package io.github.ayfri.kore.features.enchantments.effects.builders

import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.arguments.types.resources.SoundArgument
import io.github.ayfri.kore.data.sound.SoundEvent
import io.github.ayfri.kore.features.enchantments.effects.entity.*
import io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.ParticlePosition
import io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.ParticlePositionType
import io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.types.ParticleType
import io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.types.particleType
import io.github.ayfri.kore.features.enchantments.values.constantLevelBased
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.simpleStateProvider
import io.github.ayfri.kore.features.worldgen.floatproviders.constantFloatProvider
import io.github.ayfri.kore.generated.arguments.EntityTypeOrTagArgument
import io.github.ayfri.kore.generated.arguments.MobEffectOrTagArgument
import io.github.ayfri.kore.generated.arguments.types.DamageTypeArgument
import io.github.ayfri.kore.generated.arguments.types.ParticleTypeArgument
import io.github.ayfri.kore.generated.arguments.types.SoundEventArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
sealed class EntityEffectAllOfBuilder(var effects: AllOf = AllOf())

data class EntityEffectAllOfTopBuilder(var requirements: InlinableList<PredicateCondition>? = null) : EntityEffectAllOfBuilder()

fun EntityEffectAllOfBuilder.allOf(block: EntityEffectAllOfSubBuilder.() -> Unit = {}) =
	apply { effects.effects += EntityEffectAllOfSubBuilder().apply(block).effects }

fun EntityEffectAllOfTopBuilder.requirements(block: Predicate.() -> Unit = {}) =
	apply { requirements = Predicate().apply(block).predicateConditions }

class EntityEffectAllOfSubBuilder : EntityEffectAllOfBuilder()


fun EntityEffectAllOfSubBuilder.allOf(block: EntityEffectAllOfSubBuilder.() -> Unit = {}) =
	apply { effects.effects += EntityEffectAllOfSubBuilder().apply(block).effects }


fun EntityEffectAllOfBuilder.applyMobEffect(block: ApplyMobEffect.() -> Unit = {}) =
	apply { effects.effects += ApplyMobEffect().apply(block) }

fun EntityEffectAllOfBuilder.applyMobEffect(effect: MobEffectOrTagArgument, block: ApplyMobEffect.() -> Unit = {}) =
	apply { effects.effects += ApplyMobEffect(listOf(effect)).apply(block) }

fun EntityEffectAllOfBuilder.damageEntity(damageType: DamageTypeArgument, block: DamageEntity.() -> Unit = {}) =
	apply { effects.effects += DamageEntity(damageType).apply(block) }

fun EntityEffectAllOfBuilder.damageEntity(
	damageType: DamageTypeArgument,
	minDamage: Int,
	maxDamage: Int,
	block: DamageEntity.() -> Unit = {},
) =
	apply { effects.effects += DamageEntity(damageType, constantLevelBased(minDamage), constantLevelBased(maxDamage)).apply(block) }

fun EntityEffectAllOfBuilder.damageItem(block: DamageItem.() -> Unit = {}) =
	apply { effects.effects += DamageItem().apply(block) }

fun EntityEffectAllOfBuilder.damageItem(amount: Int, block: DamageItem.() -> Unit = {}) =
	apply { effects.effects += DamageItem(constantLevelBased(amount)).apply(block) }

fun EntityEffectAllOfBuilder.explode(
	attributeToUser: Boolean,
	createFire: Boolean,
	blockInteraction: BlockInteraction,
	smallParticle: ParticleTypeArgument,
	largeParticle: ParticleTypeArgument,
	sound: SoundArgument,
	block: Explode.() -> Unit = {},
) =
	apply {
		effects.effects += Explode(
			attributeToUser,
			createFire = createFire,
			blockInteraction = blockInteraction,
			smallParticle = smallParticle,
			largeParticle = largeParticle,
			sound = sound
		).apply(block)
	}

fun EntityEffectAllOfBuilder.ignite(duration: Int = 0, block: Ignite.() -> Unit = {}) =
	apply { effects.effects += Ignite(constantLevelBased(duration)).apply(block) }

fun EntityEffectAllOfBuilder.playSound(sound: SoundEventArgument, range: Float? = null, block: PlaySound.() -> Unit = {}) =
	apply { effects.effects += PlaySound(listOf(SoundEvent(sound, range))).apply(block) }

fun EntityEffectAllOfBuilder.playSound(
	sound: SoundEventArgument,
	range: Float? = null,
	volume: Float,
	pitch: Float,
	block: PlaySound.() -> Unit = {},
) =
	apply {
		effects.effects += PlaySound(
			listOf(SoundEvent(sound, range)),
			constantFloatProvider(volume),
			constantFloatProvider(pitch)
		).apply(
			block
		)
	}

fun EntityEffectAllOfBuilder.replaceBlock(blockState: BlockStateProvider = simpleStateProvider(), block: ReplaceBlock.() -> Unit = {}) =
	apply { effects.effects += ReplaceBlock(blockState).apply(block) }

fun EntityEffectAllOfBuilder.replaceDisk(blockState: BlockStateProvider = simpleStateProvider(), block: ReplaceDisk.() -> Unit = {}) =
	apply { effects.effects += ReplaceDisk(blockState).apply(block) }

fun EntityEffectAllOfBuilder.runFunction(function: FunctionArgument, block: RunFunction.() -> Unit = {}) =
	apply { effects.effects += RunFunction(function).apply(block) }

fun EntityEffectAllOfBuilder.setBlockProperties(block: SetBlockProperties.() -> Unit = {}) =
	apply { effects.effects += SetBlockProperties().apply(block) }

fun EntityEffectAllOfBuilder.spawnParticles(
	particle: ParticleType,
	horizontalPositionType: ParticlePositionType,
	verticalPositionType: ParticlePositionType,
	block: SpawnParticles.() -> Unit = {},
) =
	apply {
		effects.effects += SpawnParticles(
			particle,
			horizontalPosition = ParticlePosition(horizontalPositionType),
			verticalPosition = ParticlePosition(verticalPositionType)
		).apply(block)
	}

fun EntityEffectAllOfBuilder.spawnParticles(
	particle: ParticleTypeArgument,
	horizontalPositionType: ParticlePositionType,
	verticalPositionType: ParticlePositionType,
	block: SpawnParticles.() -> Unit = {},
) =
	apply {
		effects.effects += SpawnParticles(
			particleType(particle),
			horizontalPosition = ParticlePosition(horizontalPositionType),
			verticalPosition = ParticlePosition(verticalPositionType)
		).apply(block)
	}

fun EntityEffectAllOfBuilder.spawnParticles(
	particle: ParticleType,
	horizontalPosition: ParticlePosition,
	verticalPosition: ParticlePosition,
	block: SpawnParticles.() -> Unit = {},
) =
	apply {
		effects.effects += SpawnParticles(
			particle,
			horizontalPosition = horizontalPosition,
			verticalPosition = verticalPosition
		).apply(block)
	}

fun EntityEffectAllOfBuilder.spawnParticles(
	particle: ParticleTypeArgument,
	horizontalPosition: ParticlePosition,
	verticalPosition: ParticlePosition,
	block: SpawnParticles.() -> Unit = {},
) =
	apply {
		effects.effects += SpawnParticles(
			particleType(particle),
			horizontalPosition = horizontalPosition,
			verticalPosition = verticalPosition
		).apply(block)
	}

fun EntityEffectAllOfBuilder.summonEntity(block: SummonEntity.() -> Unit) =
	apply { effects.effects += SummonEntity().apply(block) }

fun EntityEffectAllOfBuilder.summonEntity(
	vararg entityType: EntityTypeOrTagArgument,
	joinTeam: Boolean? = null,
	block: SummonEntity.() -> Unit = {},
) =
	apply { effects.effects += SummonEntity(entityType.toList(), joinTeam).apply(block) }
