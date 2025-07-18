package io.github.ayfri.kore.features.enchantments

import io.github.ayfri.kore.features.enchantments.effects.builders.*
import io.github.ayfri.kore.generated.EnchantmentEffectComponents

fun EnchantmentEffects.ammoUse(block: ValueEffectBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.AMMO_USE] = ValueEffectBuilder().apply(block) }

fun EnchantmentEffects.armorEffectiveness(block: ValueEffectBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.ARMOR_EFFECTIVENESS] = ValueEffectBuilder().apply(block) }

fun EnchantmentEffects.attributes(block: AttributeEffectBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.ATTRIBUTES] = AttributeEffectBuilder().apply(block) }

fun EnchantmentEffects.blockExperience(block: ValueEffectBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.BLOCK_EXPERIENCE] = ValueEffectBuilder().apply(block) }

fun EnchantmentEffects.crossbowChargingSounds(block: CrossbowChargingSoundBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.CROSSBOW_CHARGING_SOUNDS] = CrossbowChargingSoundBuilder().apply(block) }

fun EnchantmentEffects.crossbowChargeTime(block: ValueEffectBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.CROSSBOW_CHARGE_TIME] = ValueEffectBuilder().apply(block) }

fun EnchantmentEffects.damage(block: ValueEffectBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.DAMAGE] = ValueEffectBuilder().apply(block) }

fun EnchantmentEffects.damageImmunity(block: EmptyConditionalEffectBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.DAMAGE_IMMUNITY] = EmptyConditionalEffectBuilder().apply(block) }

fun EnchantmentEffects.equipmentDrops(block: EquipmentDropsBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.EQUIPMENT_DROPS] = EquipmentDropsBuilder().apply(block) }

fun EnchantmentEffects.fishingLuckBonus(block: ValueEffectBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.FISHING_LUCK_BONUS] = ValueEffectBuilder().apply(block) }

fun EnchantmentEffects.fishingTimeReduction(block: ValueEffectBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.FISHING_TIME_REDUCTION] = ValueEffectBuilder().apply(block) }

fun EnchantmentEffects.hitBlock(block: EntityEffectBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.HIT_BLOCK] = EntityEffectBuilder().apply(block) }

fun EnchantmentEffects.knockback(block: ValueEffectBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.KNOCKBACK] = ValueEffectBuilder().apply(block) }

fun EnchantmentEffects.itemDamage(block: ValueEffectBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.ITEM_DAMAGE] = ValueEffectBuilder().apply(block) }

fun EnchantmentEffects.mobExperience(block: ValueEffectBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.MOB_EXPERIENCE] = ValueEffectBuilder().apply(block) }

fun EnchantmentEffects.postAttack(block: PostAttackBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.POST_ATTACK] = PostAttackBuilder().apply(block) }

fun EnchantmentEffects.preventArmorChange() =
	apply { this[EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE] = EmptyEffectBuilder }

fun EnchantmentEffects.preventEquipmentDrop() =
	apply { this[EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP] = EmptyEffectBuilder }

fun EnchantmentEffects.projectileCount(block: ValueEffectBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.PROJECTILE_COUNT] = ValueEffectBuilder().apply(block) }

fun EnchantmentEffects.projectilePiercing(block: ValueEffectBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.PROJECTILE_PIERCING] = ValueEffectBuilder().apply(block) }

fun EnchantmentEffects.projectileSpawned(block: EntityEffectBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.PROJECTILE_SPAWNED] = EntityEffectBuilder().apply(block) }

fun EnchantmentEffects.projectileSpread(block: ValueEffectBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.PROJECTILE_SPREAD] = ValueEffectBuilder().apply(block) }

fun EnchantmentEffects.repairWithXp(block: ValueEffectBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.REPAIR_WITH_XP] = ValueEffectBuilder().apply(block) }

fun EnchantmentEffects.smashDamagePerFallenBlock(block: ValueEffectBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.SMASH_DAMAGE_PER_FALLEN_BLOCK] = ValueEffectBuilder().apply(block) }

fun EnchantmentEffects.tick(block: EntityEffectBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.TICK] = EntityEffectBuilder().apply(block) }

fun EnchantmentEffects.tridentReturnAcceleration(block: ValueEffectBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.TRIDENT_RETURN_ACCELERATION] = ValueEffectBuilder().apply(block) }

fun EnchantmentEffects.tridentSpinAttackStrength(block: ValueEffectBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.TRIDENT_SPIN_ATTACK_STRENGTH] = ValueEffectBuilder().apply(block) }

fun EnchantmentEffects.tridentSound(block: TridentSoundBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.TRIDENT_SOUND] = TridentSoundBuilder().apply(block) }
