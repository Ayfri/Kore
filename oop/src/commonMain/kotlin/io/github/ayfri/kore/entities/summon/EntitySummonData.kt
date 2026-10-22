package io.github.ayfri.kore.entities.summon

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.numbers.TimeNumber
import io.github.ayfri.kore.arguments.types.literals.UUIDArgument
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.generated.arguments.types.EntityTypeArgument
import io.github.ayfri.kore.serializers.JsonSerialName
import io.github.ayfri.kore.serializers.UUIDAsIntArraySerializer
import io.github.ayfri.kore.teams.Team
import io.github.ayfri.kore.utils.encodeToNbtTag
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.set
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.NbtCompoundBuilder
import net.benwoodworth.knbt.nbtCompound

/**
 * Root NBT every entity shares, the base of [ItemEntitySummonData] and of the helpers' mannequins, display entities and
 * interactions. Its fields are written first, before the ones of the subclass: `Tags`, `CustomName`, `Glowing`...
 *
 * Extending it takes a plain `@Serializable` class, `@Serializable(with = ...)` on a parent drops the inherited fields.
 */
@Serializable
abstract class EntitySummonData {
	/** Ticks of air left. */
	@JsonSerialName("Air")
	var air: Short? = null

	@JsonSerialName("CustomName")
	var customName: ChatComponents? = null

	/** Whether the name is shown without looking at the entity. */
	@JsonSerialName("CustomNameVisible")
	var customNameVisible: Boolean? = null

	/** Free NBT stored on the entity, matched by `nbt={data:{...}}` in selectors, see [customData]. */
	var data: NbtCompound? = null

	/** Blocks the entity has fallen. */
	var fallDistance: Double? = null

	/** Ticks of fire left, or if negative, ticks until the entity starts to burn. */
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

	/** Velocity in blocks per tick on each axis, see [motion]. */
	@JsonSerialName("Motion")
	var motion: List<Double>? = null

	@JsonSerialName("NoGravity")
	var noGravity: Boolean? = null

	@JsonSerialName("OnGround")
	var onGround: Boolean? = null

	/** Entities riding this one, each with its `id`, see [passenger]. */
	@JsonSerialName("Passengers")
	var passengers: List<NbtCompound>? = null

	/** Ticks until the entity can go through a nether portal. */
	@JsonSerialName("PortalCooldown")
	var portalCooldown: Int? = null

	/** Yaw then pitch, in degrees, see [rotation]. */
	@JsonSerialName("Rotation")
	var rotation: List<Float>? = null

	@JsonSerialName("Silent")
	var silent: Boolean? = null

	@JsonSerialName("Tags")
	var tags: List<String>? = null

	/** Name of the team the entity joins when it spawns, see [team]. */
	@JsonSerialName("Team")
	var team: String? = null

	/** Ticks the entity has been freezing, see [frozen]. */
	@JsonSerialName("TicksFrozen")
	var ticksFrozen: Int? = null

	/** The entity type written as the `id` of a [passenger]. */
	abstract val entityType: EntityTypeArgument

	/** The root NBT of the entity, with every inherited field first. */
	abstract fun toNbt(): NbtCompound
}

/**
 * Spawn options of a `minecraft:item` entity, on top of the [EntitySummonData] fields. The `Item` compound isn't part of
 * [toNbt], `ItemStack.summon` adds it, so an item entity isn't possible as a [passenger].
 */
@Serializable
@SerialName("item")
data class ItemEntitySummonData(
	@JsonSerialName("Age") var age: Short? = null,
	@JsonSerialName("Health") var health: Short? = null,
	@Serializable(with = UUIDAsIntArraySerializer::class) @JsonSerialName("Owner") var owner: UUIDArgument? = null,
	@JsonSerialName("PickupDelay") var pickupDelay: Short? = null,
	@Transient var showcase: Boolean = false,
	@Serializable(with = UUIDAsIntArraySerializer::class) @JsonSerialName("Thrower") var thrower: UUIDArgument? = null,
) : EntitySummonData() {
	@Transient
	override val entityType = EntityTypes.ITEM

	override fun toNbt() = encodeToNbtTag(serializer(), this).nbtCompound
}

/**
 * Sets the free NBT stored on the entity.
 *
 * ```
 * customData { this["quest"] = "intro" }  // data:{quest:"intro"}
 * ```
 */
fun EntitySummonData.customData(block: NbtCompoundBuilder.() -> Unit) {
	data = nbt(block)
}

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
 * Sets how long the entity has been freezing, the game freezes it for that long when it isn't immune.
 *
 * ```
 * frozen(5.seconds)  // TicksFrozen:100
 * ```
 */
fun EntitySummonData.frozen(duration: TimeNumber) {
	ticksFrozen = duration.wholeTicks()
}

/**
 * Makes the entity immune to damage for [duration].
 *
 * ```
 * invulnerableFor(3.seconds)  // invulnerable_time:60
 * ```
 */
fun EntitySummonData.invulnerableFor(duration: TimeNumber) {
	invulnerableTime = duration.wholeTicks()
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
 * Sets the entity on fire for [duration], at most 32767 ticks.
 *
 * ```
 * onFire(5.seconds)  // Fire:100s
 * ```
 */
fun EntitySummonData.onFire(duration: TimeNumber) {
	fire = duration.wholeTicks().coerceAtMost(Short.MAX_VALUE.toInt()).toShort()
}

/**
 * Adds [passenger] to the entities riding this one, written with its `id` and its fields as they are now.
 *
 * ```
 * passenger(textDisplay { text("Boss") })  // Passengers:[{id:"minecraft:text_display",text:"Boss"}]
 * ```
 */
fun EntitySummonData.passenger(passenger: EntitySummonData) {
	passengers = passengers.orEmpty() + nbt {
		this["id"] = passenger.entityType.asId()
		passenger.toNbt().forEach { (key, value) -> this[key] = value }
	}
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

/**
 * Adds scoreboard tags to the entity, keeping the ones already set.
 *
 * ```
 * tag("shop", "npc")  // Tags:["shop","npc"]
 * ```
 */
fun EntitySummonData.tag(vararg names: String) {
	tags = tags.orEmpty() + names
}

/**
 * Makes the entity join [team] when it spawns.
 *
 * ```
 * team(team("red"))  // Team:"red"
 * ```
 */
fun EntitySummonData.team(team: Team) {
	this.team = team.name
}
