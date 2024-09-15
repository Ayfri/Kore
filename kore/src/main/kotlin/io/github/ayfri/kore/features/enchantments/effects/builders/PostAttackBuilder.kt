package io.github.ayfri.kore.features.enchantments.effects.builders

import io.github.ayfri.kore.arguments.types.EffectOrTagArgument
import io.github.ayfri.kore.arguments.types.EntityTypeOrTagArgument
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
import io.github.ayfri.kore.serializers.InlineSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer


@Serializable(with = PostAttackBuilder.Companion.PostAttackBuilderSerializer::class)
data class PostAttackBuilder(
	var effects: List<PostAttackConditionalEffect> = emptyList(),
) : EffectBuilder() {
	companion object {
		data object PostAttackBuilderSerializer : InlineSerializer<PostAttackBuilder, List<PostAttackConditionalEffect>>(
			kSerializer = ListSerializer(PostAttackConditionalEffect.serializer()),
			property = PostAttackBuilder::effects
		)
	}
}

fun PostAttackBuilder.allOf(
	enchanted: PostAttackSpecifier,
	affected: PostAttackSpecifier,
	block: EntityEffectAllOfBuilder.() -> Unit = {},
) = apply {
	val effect = EntityEffectAllOfTopBuilder().apply(block)
	effects += PostAttackConditionalEffect(enchanted, affected, effect.effects, effect.requirements)
}

fun PostAttackBuilder.applyMobEffect(enchanted: PostAttackSpecifier, affected: PostAttackSpecifier, block: ApplyMobEffect.() -> Unit = {}) =
	apply {
		val effect = ApplyMobEffect().apply(block)
		effects += PostAttackConditionalEffect(enchanted, affected, effect, effect.requirements)
	}

fun PostAttackBuilder.applyMobEffect(
	enchanted: PostAttackSpecifier,
	affected: PostAttackSpecifier,
	effect: EffectOrTagArgument, block: ApplyMobEffect.() -> Unit = {},
) =
	apply {
		val effect = ApplyMobEffect(listOf(effect)).apply(block)
		effects += PostAttackConditionalEffect(enchanted, affected, effect, effect.requirements)
	}

fun PostAttackBuilder.damageEntity(
	enchanted: PostAttackSpecifier,
	affected: PostAttackSpecifier,
	damageType: DamageTypeArgument, block: DamageEntity.() -> Unit = {},
) =
	apply {
		val effect = DamageEntity(damageType).apply(block)
		effects += PostAttackConditionalEffect(enchanted, affected, effect, effect.requirements)
	}

fun PostAttackBuilder.damageEntity(
	enchanted: PostAttackSpecifier, affected: PostAttackSpecifier,
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
		effects += PostAttackConditionalEffect(enchanted, affected, effect, effect.requirements)
	}

fun PostAttackBuilder.damageItem(enchanted: PostAttackSpecifier, affected: PostAttackSpecifier, block: DamageItem.() -> Unit = {}) =
	apply {
		val effect = DamageItem().apply(block)
		effects += PostAttackConditionalEffect(enchanted, affected, effect, effect.requirements)
	}

fun PostAttackBuilder.damageItem(
	enchanted: PostAttackSpecifier,
	affected: PostAttackSpecifier,
	amount: Int, block: DamageItem.() -> Unit = {},
) =
	apply {
		val effect = DamageItem(constantLevelBased(amount)).apply(block)
		effects += PostAttackConditionalEffect(enchanted, affected, effect, effect.requirements)
	}

fun PostAttackBuilder.explode(
	enchanted: PostAttackSpecifier, affected: PostAttackSpecifier,
	attributeToUser: Boolean,
	createFire: Boolean,
	blockInteraction: BlockInteraction,
	smallParticle: ParticleArgument,
	largeParticle: ParticleArgument,
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
	effects += PostAttackConditionalEffect(enchanted, affected, effect, effect.requirements)
}

fun PostAttackBuilder.ignite(
	enchanted: PostAttackSpecifier,
	affected: PostAttackSpecifier,
	duration: Int = 0,
	block: Ignite.() -> Unit = {},
) =
	apply {
		val effect = Ignite(constantLevelBased(duration)).apply(block)
		effects += PostAttackConditionalEffect(enchanted, affected, effect, effect.requirements)
	}

fun PostAttackBuilder.playSound(
	enchanted: PostAttackSpecifier,
	affected: PostAttackSpecifier,
	sound: SoundEventArgument,
	range: Float? = null,
	block: PlaySound.() -> Unit = {},
) =
	apply {
		val effect = PlaySound(SoundEvent(sound, range)).apply(block)
		effects += PostAttackConditionalEffect(enchanted, affected, effect, effect.requirements)
	}

fun PostAttackBuilder.playSound(
	enchanted: PostAttackSpecifier,
	affected: PostAttackSpecifier,
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
		effects += PostAttackConditionalEffect(enchanted, affected, effect, effect.requirements)
	}

fun PostAttackBuilder.replaceBlock(
	enchanted: PostAttackSpecifier,
	affected: PostAttackSpecifier,
	blockState: BlockStateProvider = simpleStateProvider(),
	block: ReplaceBlock.() -> Unit = {},
) =
	apply {
		val effect = ReplaceBlock(blockState).apply(block)
		effects += PostAttackConditionalEffect(enchanted, affected, effect, effect.requirements)
	}

fun PostAttackBuilder.replaceDisk(
	enchanted: PostAttackSpecifier,
	affected: PostAttackSpecifier,
	blockState: BlockStateProvider = simpleStateProvider(),
	block: ReplaceDisk.() -> Unit = {},
) =
	apply {
		val effect = ReplaceDisk(blockState).apply(block)
		effects += PostAttackConditionalEffect(enchanted, affected, effect, effect.requirements)
	}

fun PostAttackBuilder.runFunction(
	enchanted: PostAttackSpecifier,
	affected: PostAttackSpecifier,
	function: FunctionArgument,
	block: RunFunction.() -> Unit = {},
) =
	apply {
		val effect = RunFunction(function).apply(block)
		effects += PostAttackConditionalEffect(enchanted, affected, effect, effect.requirements)
	}

fun PostAttackBuilder.setBlockProperties(
	enchanted: PostAttackSpecifier,
	affected: PostAttackSpecifier,
	block: SetBlockProperties.() -> Unit = {},
) =
	apply {
		val effect = SetBlockProperties().apply(block)
		effects += PostAttackConditionalEffect(enchanted, affected, effect, effect.requirements)
	}

fun PostAttackBuilder.spawnParticles(
	enchanted: PostAttackSpecifier, affected: PostAttackSpecifier,
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
	effects += PostAttackConditionalEffect(enchanted, affected, effect, effect.requirements)
}

fun PostAttackBuilder.spawnParticles(
	enchanted: PostAttackSpecifier, affected: PostAttackSpecifier,
	particle: ParticleArgument,
	horizontalPositionType: ParticlePositionType,
	verticalPositionType: ParticlePositionType,
	block: SpawnParticles.() -> Unit = {},
) = apply {
	val effect = SpawnParticles(
		particleType(particle),
		horizontalPosition = ParticlePosition(horizontalPositionType),
		verticalPosition = ParticlePosition(verticalPositionType)
	).apply(block)
	effects += PostAttackConditionalEffect(enchanted, affected, effect, effect.requirements)
}

fun PostAttackBuilder.spawnParticles(
	enchanted: PostAttackSpecifier, affected: PostAttackSpecifier,
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
	effects += PostAttackConditionalEffect(enchanted, affected, effect, effect.requirements)
}

fun PostAttackBuilder.spawnParticles(
	enchanted: PostAttackSpecifier, affected: PostAttackSpecifier,
	particle: ParticleArgument,
	horizontalPosition: ParticlePosition,
	verticalPosition: ParticlePosition,
	block: SpawnParticles.() -> Unit = {},
) = apply {
	val effect = SpawnParticles(
		particleType(particle),
		horizontalPosition = horizontalPosition,
		verticalPosition = verticalPosition,
	).apply(block)
	effects += PostAttackConditionalEffect(enchanted, affected, effect, effect.requirements)
}

fun PostAttackBuilder.summonEntity(enchanted: PostAttackSpecifier, affected: PostAttackSpecifier, block: SummonEntity.() -> Unit) =
	apply {
		val effect = SummonEntity().apply(block)
		effects += PostAttackConditionalEffect(enchanted, affected, effect, effect.requirements)
	}

fun PostAttackBuilder.summonEntity(
	enchanted: PostAttackSpecifier, affected: PostAttackSpecifier,
	vararg entityType: EntityTypeOrTagArgument,
	joinTeam: Boolean? = null,
	block: SummonEntity.() -> Unit = {},
) = apply {
	val effect = SummonEntity(entityType.toList(), joinTeam).apply(block)
	effects += PostAttackConditionalEffect(enchanted, affected, effect, effect.requirements)
}
