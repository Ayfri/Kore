package io.github.ayfri.kore.entities.summon

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.types.literals.UUIDArgument
import io.github.ayfri.kore.serializers.JsonSerialName
import io.github.ayfri.kore.serializers.UUIDAsIntArraySerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Root NBT every entity shares, the base of [ItemEntitySummonData] and of the helpers' mannequins, display entities and
 * interactions. Fields are written first, before the ones of the subclass: `Tags`, `CustomName`, `Glowing`...
 *
 * Extending it takes a plain `@Serializable` class, `@Serializable(with = ...)` on a parent drops the inherited fields.
 */
@Serializable
abstract class EntitySummonData {
	@JsonSerialName("Air")
	var air: Int? = null

	@JsonSerialName("CustomName")
	var customName: ChatComponents? = null

	@JsonSerialName("CustomNameVisible")
	var customNameVisible: Boolean? = null
	var fallDistance: Double? = null

	@JsonSerialName("Fire")
	var fire: Short? = null

	@JsonSerialName("Glowing")
	var glowing: Boolean? = null

	@JsonSerialName("HasVisualFire")
	var hasVisualFire: Boolean? = null

	@JsonSerialName("Invulnerable")
	var invulnerable: Boolean? = null

	/** Ticks of temporary damage immunity: the entity ignores damage while this is above 0 or while [invulnerable] is true. */
	var invulnerableTime: Int? = null

	@JsonSerialName("Motion")
	var motion: List<Double>? = null

	@JsonSerialName("NoGravity")
	var noGravity: Boolean? = null

	@JsonSerialName("OnGround")
	var onGround: Boolean? = null

	@JsonSerialName("PortalCooldown")
	var portalCooldown: Int? = null

	@JsonSerialName("Pos")
	var pos: List<Double>? = null

	@JsonSerialName("Rotation")
	var rotation: List<Float>? = null

	@JsonSerialName("Silent")
	var silent: Boolean? = null

	@JsonSerialName("Tags")
	var tags: List<String>? = null
}

/** Spawn options of a `minecraft:item` entity, on top of the [EntitySummonData] fields. */
@Serializable
@SerialName("item")
data class ItemEntitySummonData(
	@JsonSerialName("Age") var age: Short? = null,
	@JsonSerialName("Health") var health: Short? = null,
	@Serializable(with = UUIDAsIntArraySerializer::class) @JsonSerialName("Owner") var owner: UUIDArgument? = null,
	@JsonSerialName("PickupDelay") var pickupDelay: Short? = null,
	@Transient var showcase: Boolean = false,
	@Serializable(with = UUIDAsIntArraySerializer::class) @JsonSerialName("Thrower") var thrower: UUIDArgument? = null,
) : EntitySummonData()

fun itemEntitySummonData(block: ItemEntitySummonData.() -> Unit) = ItemEntitySummonData().apply(block)

/**
 * Sets the custom name of the entity to a plain or colored text.
 *
 * ```
 * customName("Shop", Color.GOLD)  // CustomName:{text:"Shop",color:"gold"}
 * ```
 */
fun EntitySummonData.customName(text: String, color: Color? = null) {
	customName = textComponent(text, color)
}

/**
 * Sets the velocity of the entity, in blocks per tick on each axis.
 *
 * ```
 * motion(0.0, 0.6, 0.0)  // Motion:[0.0d,0.6d,0.0d]
 * ```
 */
fun EntitySummonData.motion(dx: Double, dy: Double, dz: Double) {
	motion = listOf(dx, dy, dz)
}

/**
 * Sets the yaw and pitch of the entity, in degrees.
 *
 * ```
 * rotation(90f, 0f)  // Rotation:[90.0f,0.0f]
 * ```
 */
fun EntitySummonData.rotation(yaw: Float, pitch: Float) {
	rotation = listOf(yaw, pitch)
}
