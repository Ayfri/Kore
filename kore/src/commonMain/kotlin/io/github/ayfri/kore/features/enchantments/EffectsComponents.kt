package io.github.ayfri.kore.features.enchantments

import io.github.ayfri.kore.features.enchantments.effects.builders.*
import io.github.ayfri.kore.generated.EnchantmentEffectComponents

/** Changes how much ammunition a shot consumes, `0` sparing it entirely, as Infinity does. */
fun EnchantmentEffects.ammoUse(block: ValueEffectBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.AMMO_USE] = ValueEffectBuilder().apply(block) }

/** Changes how much of the armor of the victim counts against the hit, `0` ignoring it, as Breach does. */
fun EnchantmentEffects.armorEffectiveness(block: ValueEffectBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.ARMOR_EFFECTIVENESS] = ValueEffectBuilder().apply(block) }

/** Applies attribute modifiers while the enchanted item sits in one of the slots of the enchantment. */
fun EnchantmentEffects.attributes(block: AttributeEffectBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.ATTRIBUTES] = AttributeEffectBuilder().apply(block) }

/** Changes how much experience a mined block drops. */
fun EnchantmentEffects.blockExperience(block: ValueEffectBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.BLOCK_EXPERIENCE] = ValueEffectBuilder().apply(block) }

/**
 * Changes how long a crossbow takes to charge, in seconds, as Quick Charge does.
 *
 * The component holds a single effect, so only the last one built is kept and `requirements { }` is not honored.
 */
fun EnchantmentEffects.crossbowChargeTime(block: SingleValueEffectBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.CROSSBOW_CHARGE_TIME] = SingleValueEffectBuilder().apply(block) }

/** Replaces the sounds a crossbow plays while charging, one entry per enchantment level. */
fun EnchantmentEffects.crossbowChargingSounds(block: CrossbowChargingSoundBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.CROSSBOW_CHARGING_SOUNDS] = CrossbowChargingSoundBuilder().apply(block) }

/** Changes how much damage a hit deals, as Sharpness does. */
fun EnchantmentEffects.damage(block: ValueEffectBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.DAMAGE] = ValueEffectBuilder().apply(block) }

/** Cancels the damage of matching hits entirely, as Frost Walker does with the damage of the blocks it freezes. */
fun EnchantmentEffects.damageImmunity(block: EmptyConditionalEffectBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.DAMAGE_IMMUNITY] = EmptyConditionalEffectBuilder().apply(block) }

/** Adds protection points against matching hits, each point cutting the remaining damage by 4%, as Protection does. */
fun EnchantmentEffects.damageProtection(block: ValueEffectBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.DAMAGE_PROTECTION] = ValueEffectBuilder().apply(block) }

/** Changes how likely a killed entity is to drop its equipment, as Looting does. */
fun EnchantmentEffects.equipmentDrops(block: EquipmentDropsBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.EQUIPMENT_DROPS] = EquipmentDropsBuilder().apply(block) }

/** Changes the luck applied to the fishing loot table, as Luck of the Sea does. */
fun EnchantmentEffects.fishingLuckBonus(block: ValueEffectBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.FISHING_LUCK_BONUS] = ValueEffectBuilder().apply(block) }

/** Changes how long a catch takes, in seconds, as Lure does. */
fun EnchantmentEffects.fishingTimeReduction(block: ValueEffectBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.FISHING_TIME_REDUCTION] = ValueEffectBuilder().apply(block) }

/** Runs effects when a projectile shot by the holder hits a block. */
fun EnchantmentEffects.hitBlock(block: EntityEffectBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.HIT_BLOCK] = EntityEffectBuilder().apply(block) }

/** Changes how much durability an item loses when used, as Unbreaking does. */
fun EnchantmentEffects.itemDamage(block: ValueEffectBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.ITEM_DAMAGE] = ValueEffectBuilder().apply(block) }

/** Changes how far a hit knocks the victim back, as Knockback does. */
fun EnchantmentEffects.knockback(block: ValueEffectBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.KNOCKBACK] = ValueEffectBuilder().apply(block) }

/**
 * Runs effects when the block position of the holder changes, when it equips the item and when it loads, as Frost
 * Walker does to freeze the water it steps on.
 */
fun EnchantmentEffects.locationChanged(block: EntityEffectBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.LOCATION_CHANGED] = EntityEffectBuilder().apply(block) }

/** Changes how much experience a killed mob drops. */
fun EnchantmentEffects.mobExperience(block: ValueEffectBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.MOB_EXPERIENCE] = ValueEffectBuilder().apply(block) }

/** Runs effects right after a hit lands, as Fire Aspect and Wind Burst do. */
fun EnchantmentEffects.postAttack(block: PostAttackBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.POST_ATTACK] = PostAttackBuilder().apply(block) }

/** Runs effects right after a piercing projectile goes through an entity, landing on the entity pierced. */
fun EnchantmentEffects.postPiercingAttack(block: EntityEffectBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.POST_PIERCING_ATTACK] = EntityEffectBuilder().apply(block) }

/** Keeps the holder from taking the enchanted armor off, as Curse of Binding does. */
fun EnchantmentEffects.preventArmorChange() =
	apply { this[EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE] = EmptyEffectBuilder }

/** Keeps the enchanted item from dropping on death, as Curse of Vanishing does. */
fun EnchantmentEffects.preventEquipmentDrop() =
	apply { this[EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP] = EmptyEffectBuilder }

/** Changes how many projectiles a shot fires, as Multishot does. */
fun EnchantmentEffects.projectileCount(block: ValueEffectBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.PROJECTILE_COUNT] = ValueEffectBuilder().apply(block) }

/** Changes how many entities a projectile goes through, as Piercing does. */
fun EnchantmentEffects.projectilePiercing(block: ValueEffectBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.PROJECTILE_PIERCING] = ValueEffectBuilder().apply(block) }

/** Runs effects on each projectile the holder shoots, as Flame does to set arrows on fire. */
fun EnchantmentEffects.projectileSpawned(block: EntityEffectBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.PROJECTILE_SPAWNED] = EntityEffectBuilder().apply(block) }

/** Changes the angle, in degrees, the extra projectiles of a shot spread over, as Multishot does. */
fun EnchantmentEffects.projectileSpread(block: ValueEffectBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.PROJECTILE_SPREAD] = ValueEffectBuilder().apply(block) }

/** Changes how much durability each experience orb repairs, as Mending does. */
fun EnchantmentEffects.repairWithXp(block: ValueEffectBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.REPAIR_WITH_XP] = ValueEffectBuilder().apply(block) }

/** Changes how much extra damage a mace smash deals per block fallen, as Density does. */
fun EnchantmentEffects.smashDamagePerFallenBlock(block: ValueEffectBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.SMASH_DAMAGE_PER_FALLEN_BLOCK] = ValueEffectBuilder().apply(block) }

/** Runs effects every tick while the item is equipped, as Soul Speed does. */
fun EnchantmentEffects.tick(block: EntityEffectBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.TICK] = EntityEffectBuilder().apply(block) }

/** Changes how fast a thrown trident flies back to its owner, as Loyalty does. */
fun EnchantmentEffects.tridentReturnAcceleration(block: ValueEffectBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.TRIDENT_RETURN_ACCELERATION] = ValueEffectBuilder().apply(block) }

/** Replaces the sounds a Riptide trident plays when it launches its holder, one entry per enchantment level. */
fun EnchantmentEffects.tridentSound(block: TridentSoundBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.TRIDENT_SOUND] = TridentSoundBuilder().apply(block) }

/**
 * Changes how far a Riptide trident launches its holder.
 *
 * The component holds a single effect, so only the last one built is kept and `requirements { }` is not honored.
 */
fun EnchantmentEffects.tridentSpinAttackStrength(block: SingleValueEffectBuilder.() -> Unit = {}) =
	apply { this[EnchantmentEffectComponents.TRIDENT_SPIN_ATTACK_STRENGTH] = SingleValueEffectBuilder().apply(block) }
