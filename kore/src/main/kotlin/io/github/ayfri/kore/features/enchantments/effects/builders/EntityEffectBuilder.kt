package io.github.ayfri.kore.features.enchantments.effects.builders

import io.github.ayfri.kore.arguments.types.resources.*
import io.github.ayfri.kore.data.sound.SoundEvent
import io.github.ayfri.kore.features.enchantments.effects.entity.*
import io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.ParticlePosition
import io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.ParticlePositionType
import io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.types.ParticleType
import io.github.ayfri.kore.features.enchantments.effects.entity.spawnparticles.types.particleType
import io.github.ayfri.kore.features.enchantments.values.constantLevelBased
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.simpleStateProvider
import io.github.ayfri.kore.features.worldgen.floatproviders.constantFloatProvider
import io.github.ayfri.kore.generated.arguments.EntityTypeOrTagArgument
import io.github.ayfri.kore.generated.arguments.MobEffectOrTagArgument
import io.github.ayfri.kore.generated.arguments.types.DamageTypeArgument
import io.github.ayfri.kore.generated.arguments.types.ParticleTypeArgument
import io.github.ayfri.kore.generated.arguments.types.SoundEventArgument
import io.github.ayfri.kore.serializers.InlineSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer

@Serializable(with = EntityEffectBuilder.Companion.EntityEffectBuilderSerializer::class)
data class EntityEffectBuilder(
	var effects: List<ConditionalEffect> = emptyList(),
) : EffectBuilder() {
	companion object {
		data object EntityEffectBuilderSerializer : InlineSerializer<EntityEffectBuilder, List<ConditionalEffect>>(
			kSerializer = ListSerializer(ConditionalEffect.serializer()),
			property = EntityEffectBuilder::effects
		)
	}
}

fun EntityEffectBuilder.allOf(block: EntityEffectAllOfTopBuilder.() -> Unit = {}) = apply {
	val effect = EntityEffectAllOfTopBuilder().apply(block)
	effects += ConditionalEffect(effect.effects, effect.requirements)
}

fun EntityEffectBuilder.applyMobEffect(block: ApplyMobEffect.() -> Unit = {}) =
	apply {
		val effect = ApplyMobEffect().apply(block)
		effects += ConditionalEffect(effect, effect.requirements)
	}

fun EntityEffectBuilder.applyMobEffect(effect: MobEffectOrTagArgument, block: ApplyMobEffect.() -> Unit = {}) =
	apply {
		val effect = ApplyMobEffect(listOf(effect)).apply(block)
		effects += ConditionalEffect(effect, effect.requirements)
	}

fun EntityEffectBuilder.damageEntity(damageType: DamageTypeArgument, block: DamageEntity.() -> Unit = {}) =
	apply {
		val effect = DamageEntity(damageType).apply(block)
		effects += ConditionalEffect(effect, effect.requirements)
	}

fun EntityEffectBuilder.damageEntity(
	damageType: DamageTypeArgument,
	minDamage: Int,
	maxDamage: Int,
	block: DamageEntity.() -> Unit = {},
) =
	apply {
		val effect = DamageEntity(
			damageType,
			constantLevelBased(minDamage),
			constantLevelBased(maxDamage)
		).apply(block)
		effects += ConditionalEffect(effect, effect.requirements)
	}

fun EntityEffectBuilder.changeItemDamage(block: DamageItem.() -> Unit = {}) =
	apply {
		val effect = DamageItem().apply(block)
		effects += ConditionalEffect(effect, effect.requirements)
	}

fun EntityEffectBuilder.changeItemDamage(amount: Int, block: DamageItem.() -> Unit = {}) =
	apply {
		val effect = DamageItem(constantLevelBased(amount)).apply(block)
		effects += ConditionalEffect(effect, effect.requirements)
	}

fun EntityEffectBuilder.explode(
	attributeToUser: Boolean,
	createFire: Boolean,
	blockInteraction: BlockInteraction,
	smallParticle: ParticleTypeArgument,
	largeParticle: ParticleTypeArgument,
	sound: SoundArgument,
	block: Explode.() -> Unit = {},
) = apply {
	val effect = Explode(
		attributeToUser,
		createFire = createFire,
		blockInteraction = blockInteraction,
		smallParticle = smallParticle,
		largeParticle = largeParticle,
		sound = sound
	).apply(block)
	effects += ConditionalEffect(effect, effect.requirements)
}

fun EntityEffectBuilder.ignite(duration: Int = 0, block: Ignite.() -> Unit = {}) =
	apply {
		val effect = Ignite(constantLevelBased(duration)).apply(block)
		effects += ConditionalEffect(effect, effect.requirements)
	}

fun EntityEffectBuilder.playSound(sound: SoundEventArgument, range: Float? = null, block: PlaySound.() -> Unit = {}) =
	apply {
		val effect = PlaySound(SoundEvent(sound, range)).apply(block)
		effects += ConditionalEffect(effect, effect.requirements)
	}

fun EntityEffectBuilder.playSound(
	sound: SoundEventArgument,
	range: Float? = null,
	volume: Float,
	pitch: Float,
	block: PlaySound.() -> Unit = {},
) =
	apply {
		val effect = PlaySound(
			SoundEvent(sound, range),
			constantFloatProvider(volume),
			constantFloatProvider(pitch)
		).apply(block)
		effects += ConditionalEffect(effect, effect.requirements)
	}

fun EntityEffectBuilder.replaceBlock(blockState: BlockStateProvider = simpleStateProvider(), block: ReplaceBlock.() -> Unit = {}) =
	apply {
		val effect = ReplaceBlock(blockState).apply(block)
		effects += ConditionalEffect(effect, effect.requirements)
	}

fun EntityEffectBuilder.replaceDisk(blockState: BlockStateProvider = simpleStateProvider(), block: ReplaceDisk.() -> Unit = {}) =
	apply {
		val effect = ReplaceDisk(blockState).apply(block)
		effects += ConditionalEffect(effect, effect.requirements)
	}

fun EntityEffectBuilder.runFunction(function: FunctionArgument, block: RunFunction.() -> Unit = {}) =
	apply {
		val effect = RunFunction(function).apply(block)
		effects += ConditionalEffect(effect, effect.requirements)
	}

fun EntityEffectBuilder.setBlockProperties(block: SetBlockProperties.() -> Unit = {}) =
	apply {
		val effect = SetBlockProperties().apply(block)
		effects += ConditionalEffect(effect, effect.requirements)
	}

fun EntityEffectBuilder.spawnParticles(
	particle: ParticleType,
	horizontalPositionType: ParticlePositionType,
	verticalPositionType: ParticlePositionType,
	block: SpawnParticles.() -> Unit = {},
) = apply {
	val effect = SpawnParticles(
		particle,
		horizontalPosition = ParticlePosition(horizontalPositionType),
		verticalPosition = ParticlePosition(verticalPositionType)
	).apply(block)
	effects += ConditionalEffect(effect, effect.requirements)
}

fun EntityEffectBuilder.spawnParticles(
	particle: ParticleTypeArgument,
	horizontalPositionType: ParticlePositionType,
	verticalPositionType: ParticlePositionType,
	block: SpawnParticles.() -> Unit = {},
) = apply {
	val effect = SpawnParticles(
		particleType(particle),
		horizontalPosition = ParticlePosition(horizontalPositionType),
		verticalPosition = ParticlePosition(verticalPositionType)
	).apply(block)
	effects += ConditionalEffect(effect, effect.requirements)
}

fun EntityEffectBuilder.spawnParticles(
	particle: ParticleType,
	horizontalPosition: ParticlePosition,
	verticalPosition: ParticlePosition,
	block: SpawnParticles.() -> Unit = {},
) = apply {
	val effect = SpawnParticles(
		particle,
		horizontalPosition = horizontalPosition,
		verticalPosition = verticalPosition,
	).apply(block)
	effects += ConditionalEffect(effect, effect.requirements)
}

fun EntityEffectBuilder.spawnParticles(
	particle: ParticleTypeArgument,
	horizontalPosition: ParticlePosition,
	verticalPosition: ParticlePosition,
	block: SpawnParticles.() -> Unit = {},
) = apply {
	val effect = SpawnParticles(
		particleType(particle),
		horizontalPosition = horizontalPosition,
		verticalPosition = verticalPosition,
	).apply(block)
	effects += ConditionalEffect(effect, effect.requirements)
}

fun EntityEffectBuilder.summonEntity(block: SummonEntity.() -> Unit) =
	apply {
		val effect = SummonEntity().apply(block)
		effects += ConditionalEffect(effect, effect.requirements)
	}

fun EntityEffectBuilder.summonEntity(
	vararg entityType: EntityTypeOrTagArgument,
	joinTeam: Boolean? = null,
	block: SummonEntity.() -> Unit = {},
) = apply {
	val effect = SummonEntity(entityType.toList(), joinTeam).apply(block)
	effects += ConditionalEffect(effect, effect.requirements)
}
