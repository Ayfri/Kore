package io.github.ayfri.kore.data.particle

import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.colors.ColorAsARGBDecimalSerializer
import io.github.ayfri.kore.arguments.colors.ColorAsDecimalSerializer
import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.data.item.ItemStack
import io.github.ayfri.kore.generated.Particles
import io.github.ayfri.kore.generated.arguments.types.ParticleTypeArgument
import io.github.ayfri.kore.serializers.TripleAsArray
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.Serializable

/**
 * A particle textured after [blockState]: `block`, `block_crumble`, `block_marker`, `dust_pillar` and `falling_dust`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Particle_format#block
 *
 * @property type The id of the particle.
 * @property blockState The block the particle takes its texture from.
 */
@Serializable
data class BlockParticleOptions(
	override var type: ParticleTypeArgument,
	var blockState: BlockState,
) : ParticleOptions()

/**
 * Creates a block-textured particle, `block` unless [type] says otherwise. The states of [block] are kept.
 *
 * ```
 * blockParticle(Blocks.STONE_SLAB(states = mapOf("half" to "top")))
 * blockParticle(Blocks.SAND, Particles.DUST_PILLAR)
 * ```
 */
fun blockParticle(block: BlockArgument, type: ParticleTypeArgument = Particles.BLOCK) =
	BlockParticleOptions(type, BlockState(block))

/**
 * The position a `vibration` particle travels to.
 *
 * @property pos The `[X, Y, Z]` block position.
 * @property type The kind of position source, always `block`: the game doesn't accept another one here.
 */
@Serializable
data class BlockPositionSource(
	var pos: TripleAsArray<Int, Int, Int>,
	@EncodeDefault val type: String = "block",
)

/**
 * A particle tinted by an ARGB color: `entity_effect`, `flash` and `tinted_leaves`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Particle_format#entity_effect
 *
 * @property type The id of the particle.
 * @property color The ARGB tint of the particle, a color without alpha being fully opaque.
 */
@Serializable
data class ColorParticleOptions(
	override var type: ParticleTypeArgument,
	@Serializable(ColorAsARGBDecimalSerializer::class) var color: Color,
) : ParticleOptions()

/**
 * Creates an ARGB-tinted particle, `entity_effect` unless [type] says otherwise.
 *
 * ```
 * colorParticle(Color.GREEN)                       // {"type": "minecraft:entity_effect", "color": -11141291}
 * colorParticle(Color.WHITE, Particles.FLASH)      // {"type": "minecraft:flash", "color": -1}
 * ```
 */
fun colorParticle(color: Color, type: ParticleTypeArgument = Particles.ENTITY_EFFECT) = ColorParticleOptions(type, color)

/**
 * The `dragon_breath` particle.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Particle_format#dragon_breath
 *
 * @property type The id of the particle.
 * @property power The multiplier of the initial velocity, the vanilla default when `null`.
 */
@Serializable
data class DragonBreathParticleOptions(
	override var type: ParticleTypeArgument,
	var power: Float? = null,
) : ParticleOptions()

/**
 * Creates the `dragon_breath` particle.
 *
 * ```
 * dragonBreathParticle(0.5f) // {"type": "minecraft:dragon_breath", "power": 0.5}
 * ```
 */
fun dragonBreathParticle(power: Float? = null) = DragonBreathParticleOptions(Particles.DRAGON_BREATH, power)

/**
 * The `dust_color_transition` particle, fading from [fromColor] to [toColor] as it lives.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Particle_format#dust_color_transition
 *
 * @property type The id of the particle.
 * @property fromColor The RGB tint the particle starts at.
 * @property scale The size factor of the particle.
 * @property toColor The RGB tint the particle ends at.
 */
@Serializable
data class DustColorTransitionParticleOptions(
	override var type: ParticleTypeArgument,
	@Serializable(ColorAsDecimalSerializer::class) var fromColor: Color,
	var scale: Float,
	@Serializable(ColorAsDecimalSerializer::class) var toColor: Color,
) : ParticleOptions()

/**
 * Creates the `dust_color_transition` particle.
 *
 * ```
 * dustColorTransitionParticle(Color.BLUE, Color.RED, scale = 2f)
 * // {"type": "minecraft:dust_color_transition", "from_color": 5592575, "scale": 2.0, "to_color": 16733525}
 * ```
 */
fun dustColorTransitionParticle(fromColor: Color, toColor: Color, scale: Float = 1f) =
	DustColorTransitionParticleOptions(Particles.DUST_COLOR_TRANSITION, fromColor, scale, toColor)

/**
 * The `dust` particle, tinted [color] and drawn at [scale] times its usual size.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Particle_format#dust
 *
 * @property type The id of the particle.
 * @property color The RGB tint of the particle.
 * @property scale The size factor of the particle.
 */
@Serializable
data class DustParticleOptions(
	override var type: ParticleTypeArgument,
	@Serializable(ColorAsDecimalSerializer::class) var color: Color,
	var scale: Float,
) : ParticleOptions()

/**
 * Creates the `dust` particle.
 *
 * ```
 * dustParticle(Color.RED, 1.5f) // {"type": "minecraft:dust", "color": 16733525, "scale": 1.5}
 * ```
 */
fun dustParticle(color: Color, scale: Float = 1f) = DustParticleOptions(Particles.DUST, color, scale)

/**
 * A particle of a potion effect: `effect` and `instant_effect`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Particle_format#effect
 *
 * @property type The id of the particle.
 * @property color The RGB tint of the particle, the vanilla default when `null`.
 * @property power The multiplier of the initial velocity, the vanilla default when `null`.
 */
@Serializable
data class EffectParticleOptions(
	override var type: ParticleTypeArgument,
	@Serializable(ColorAsDecimalSerializer::class) var color: Color? = null,
	var power: Float? = null,
) : ParticleOptions()

/**
 * Creates a potion effect particle, `effect` unless [type] says otherwise.
 *
 * ```
 * effectParticle(Color.GREEN, 2f) // {"type": "minecraft:effect", "color": 5635925, "power": 2.0}
 * ```
 */
fun effectParticle(color: Color? = null, power: Float? = null, type: ParticleTypeArgument = Particles.EFFECT) =
	EffectParticleOptions(type, color, power)

/**
 * A particle spawned above a column of [waterBlocks] water blocks and pushed upwards by [burstImpulseBase]:
 * `geyser_base` and `geyser_poof`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Particle_format#geyser_base
 *
 * @property type The id of the particle.
 * @property burstImpulseBase The factor applied to the initial burst impulse.
 * @property waterBlocks The height of the water column, scaling the size and the burst impulse of the particle.
 */
@Serializable
data class GeyserBurstParticleOptions(
	override var type: ParticleTypeArgument,
	var burstImpulseBase: Float,
	var waterBlocks: Int,
) : ParticleOptions()

/**
 * Creates a `geyser_base` particle, or the one [type] names.
 *
 * ```
 * geyserBurstParticle(3, 0.5f) // {"type": "minecraft:geyser_base", "burst_impulse_base": 0.5, "water_blocks": 3}
 * ```
 */
fun geyserBurstParticle(waterBlocks: Int, burstImpulseBase: Float, type: ParticleTypeArgument = Particles.GEYSER_BASE) =
	GeyserBurstParticleOptions(type, burstImpulseBase, waterBlocks)

/**
 * A particle spawned above a column of [waterBlocks] water blocks: `geyser` and `geyser_plume`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Particle_format#geyser
 *
 * @property type The id of the particle.
 * @property waterBlocks The height of the water column, scaling the size and the burst impulse of the particle.
 */
@Serializable
data class GeyserParticleOptions(
	override var type: ParticleTypeArgument,
	var waterBlocks: Int,
) : ParticleOptions()

/**
 * Creates a `geyser` particle, or the one [type] names.
 *
 * ```
 * geyserParticle(4, Particles.GEYSER_PLUME) // {"type": "minecraft:geyser_plume", "water_blocks": 4}
 * ```
 */
fun geyserParticle(waterBlocks: Int, type: ParticleTypeArgument = Particles.GEYSER) = GeyserParticleOptions(type, waterBlocks)

/**
 * The `item` particle, textured after [item].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Particle_format#item
 *
 * @property type The id of the particle.
 * @property item The item stack the particle takes its texture from.
 */
@Serializable
data class ItemParticleOptions(
	override var type: ParticleTypeArgument,
	var item: ItemStack,
) : ParticleOptions()

/**
 * Creates the `item` particle, keeping the components of [item].
 *
 * ```
 * itemParticle(Items.DIAMOND) // {"type": "minecraft:item", "item": {"id": "minecraft:diamond"}}
 * ```
 */
fun itemParticle(item: ItemArgument) = ItemParticleOptions(Particles.ITEM, ItemStack(item))

/**
 * The `sculk_charge` particle.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Particle_format#sculk_charge
 *
 * @property type The id of the particle.
 * @property roll The angle the texture is rotated to, in radians.
 */
@Serializable
data class SculkChargeParticleOptions(
	override var type: ParticleTypeArgument,
	var roll: Float,
) : ParticleOptions()

/**
 * Creates the `sculk_charge` particle, rotated by [roll] radians.
 *
 * ```
 * sculkChargeParticle(1.5f) // {"type": "minecraft:sculk_charge", "roll": 1.5}
 * ```
 */
fun sculkChargeParticle(roll: Float) = SculkChargeParticleOptions(Particles.SCULK_CHARGE, roll)

/**
 * The `shriek` particle.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Particle_format#shriek
 *
 * @property type The id of the particle.
 * @property delay The ticks before the particle renders.
 */
@Serializable
data class ShriekParticleOptions(
	override var type: ParticleTypeArgument,
	var delay: Int,
) : ParticleOptions()

/**
 * Creates the `shriek` particle, rendering after [delay] ticks.
 *
 * ```
 * shriekParticle(5) // {"type": "minecraft:shriek", "delay": 5}
 * ```
 */
fun shriekParticle(delay: Int) = ShriekParticleOptions(Particles.SHRIEK, delay)

/**
 * A particle that has no options: every one not covered by another subtype.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Particle_format
 *
 * @property type The id of the particle.
 */
@Serializable
data class SimpleParticleOptions(
	override var type: ParticleTypeArgument,
) : ParticleOptions()

/**
 * Creates a particle that has no options.
 *
 * ```
 * simpleParticle(Particles.ASH) // {"type": "minecraft:ash"}
 * ```
 */
fun simpleParticle(type: ParticleTypeArgument) = SimpleParticleOptions(type)

/**
 * The `trail` particle, flying to [target].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Particle_format#trail
 *
 * @property type The id of the particle.
 * @property color The RGB tint of the particle.
 * @property duration The ticks the particle takes to reach [target].
 * @property target The `[X, Y, Z]` position the particle flies to.
 */
@Serializable
data class TrailParticleOptions(
	override var type: ParticleTypeArgument,
	@Serializable(ColorAsDecimalSerializer::class) var color: Color,
	var duration: Int,
	var target: TripleAsArray<Double, Double, Double>,
) : ParticleOptions()

/**
 * Creates the `trail` particle, flying to [x], [y], [z] in [duration] ticks.
 *
 * ```
 * trailParticle(Color.BLUE, 20, 0.5, 64.0, 0.5)
 * // {"type": "minecraft:trail", "color": 5592575, "duration": 20, "target": [0.5, 64.0, 0.5]}
 * ```
 */
fun trailParticle(color: Color, duration: Int, x: Double, y: Double, z: Double) =
	TrailParticleOptions(Particles.TRAIL, color, duration, Triple(x, y, z))

/**
 * The `vibration` particle, travelling to [destination].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Particle_format#vibration
 *
 * @property type The id of the particle.
 * @property arrivalInTicks The ticks the particle takes to reach [destination].
 * @property destination The block position the particle travels to.
 */
@Serializable
data class VibrationParticleOptions(
	override var type: ParticleTypeArgument,
	var arrivalInTicks: Int,
	var destination: BlockPositionSource,
) : ParticleOptions()

/**
 * Creates the `vibration` particle, travelling to the block at [x], [y], [z] in [arrivalInTicks] ticks.
 * A vibration targeting an entity isn't possible because the game only accepts a `block` position source here.
 *
 * ```
 * vibrationParticle(1, 2, 3, 10)
 * // {"type": "minecraft:vibration", "arrival_in_ticks": 10, "destination": {"pos": [1, 2, 3], "type": "block"}}
 * ```
 */
fun vibrationParticle(x: Int, y: Int, z: Int, arrivalInTicks: Int) =
	VibrationParticleOptions(Particles.VIBRATION, arrivalInTicks, BlockPositionSource(Triple(x, y, z)))
